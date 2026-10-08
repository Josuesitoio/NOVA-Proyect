import { Motor3DManager } from './engine3d/Motor3DManager.js';
import { NovaApi } from './api/ApiClient.js';

console.log('Orquestador App Nova inicializado. Suministrando energia al motor...');

window.addEventListener('DOMContentLoaded', () => {
    // ==========================================
    // 1. INICIALIZACIÓN DEL MOTOR GRÁFICO (WebGL)
    // ==========================================
    const canvas = document.getElementById('novaCanvas');
    if (!canvas) {
        console.error("Error Crítico: No se encontró el elemento <canvas id='novaCanvas'> en el HTML.");
        return; // Detiene la ejecución para no generar errores en cascada
    }
    const motorManager = new Motor3DManager(canvas);

    // ==========================================
    // 2. REFERENCIAS A LA INTERFAZ DE USUARIO
    // ==========================================
    const inputKep = document.getElementById('kepidInput');
    const btnSearch = document.getElementById('searchBtn');
    const msgLoading = document.getElementById('loadingMessage');
    const msgError = document.getElementById('errorMessage');
    const panelData = document.getElementById('dataDisplay');

    // ==========================================
    // 3. ACTUALIZACIÓN VISUAL DEL INDICADOR IA
    // ==========================================
    const actualizarHUDIA = (respuesta) => {
        const flagElement = document.getElementById('aiFlag');
        if (!flagElement) return;

        // Convertimos el decimal (ej. 0.86) a porcentaje visual (86.00%)
        const porcentaje = respuesta.probabilidadExoplaneta
            ? (respuesta.probabilidadExoplaneta * 100).toFixed(2)
            : 0.00;

        if (respuesta.confirmadoPorIA) {
            flagElement.textContent = `IA: EXOPLANETA CONFIRMADO (${porcentaje}%)`;
            flagElement.style.borderColor = '#00ff00';
            flagElement.style.color = '#00ff00';
            flagElement.style.background = 'rgba(0, 255, 0, 0.1)';
            flagElement.style.textShadow = '0 0 8px #00ff00';
        } else {
            flagElement.textContent = `IA: FALSO POSITIVO / ANOMALÍA (${porcentaje}%)`;
            flagElement.style.borderColor = 'var(--nasa-red)';
            flagElement.style.color = 'var(--nasa-red)';
            flagElement.style.background = 'rgba(252, 61, 33, 0.1)';
            flagElement.style.textShadow = '0 0 8px var(--nasa-red)';
        }
    };

    // ==========================================
    // 4. EXPLORACIÓN DE LA BASE DE DATOS LOCAL
    // ==========================================
    const realizarExploracion = async (kepid) => {
        if (!kepid || kepid.trim() === "") return;
        console.log('Iniciando exploracion en Base de Datos para: ' + kepid);

        if (panelData) panelData.classList.add('hide');
        if (msgError) msgError.classList.add('hide');
        if (msgLoading) msgLoading.classList.remove('hide');

        const respuesta = await NovaApi.getTelemetriaExoplaneta(kepid);
        if (msgLoading) msgLoading.classList.add('hide');

        if (respuesta.error) {
            console.error('Exploracion fallida: ' + respuesta.message);
            if (msgError) {
                msgError.textContent = respuesta.message;
                msgError.classList.remove('hide');
            }
        } else {
            motorManager.inyectarTelemetriaNASA(respuesta);
            motorManager.reanudarMotor();

            document.getElementById('valKepid').textContent = respuesta.kepid || '--';
            document.getElementById('valTemp').textContent = respuesta.temperaturaEstelar || '--';
            document.getElementById('valGravedad').textContent = respuesta.gravedadEstelar || '--';
            document.getElementById('valRadio').textContent = respuesta.radioPlanetario || '--';
            document.getElementById('valDia').textContent = respuesta.duracionOrbital || '--';
            document.getElementById('valTempEq').textContent = respuesta.temperaturaEquilibrio || '--';

            actualizarHUDIA(respuesta);
            if (panelData) panelData.classList.remove('hide');
        }
    };

    if (btnSearch) {
        btnSearch.addEventListener('click', () => realizarExploracion(inputKep.value));
    }

    if (inputKep) {
        inputKep.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') realizarExploracion(inputKep.value);
        });
    }

    // Enlaces de demostración rápida
    document.querySelectorAll('.quick-list .kep-link').forEach(link => {
        link.addEventListener('click', (e) => {
            const kepId = e.target.getAttribute('data-kep');
            if (kepId) {
                if (inputKep) inputKep.value = kepId;
                realizarExploracion(kepId);
            }
        });
    });

    // ==========================================
    // 5. CARGA MASIVA DE DATASET (CSV)
    // ==========================================
    const btnUploadCsv = document.getElementById('uploadCsvBtn');
    if (btnUploadCsv) {
        btnUploadCsv.addEventListener('click', async (e) => {
            e.preventDefault();
            const fileInput = document.getElementById('csvFileInput');
            const csvStatus = document.getElementById('csvStatusMessage');

            if (!fileInput || fileInput.files.length === 0) {
                if (csvStatus) csvStatus.textContent = "Seleccione un archivo primero.";
                return;
            }
            if (csvStatus) csvStatus.textContent = "Ingestando base de datos...";
            const resultado = await NovaApi.subirDatasetNasa(fileInput.files[0]);
            if (csvStatus) csvStatus.textContent = resultado.error ? resultado.message : resultado.mensaje;
        });
    }

    // ==========================================
    // 6. INFERENCIA AUTOMÁTICA CON ARCHIVO JSON
    // ==========================================
    const btnUploadJson = document.getElementById('uploadJsonBtn');
    if (btnUploadJson) {
        btnUploadJson.addEventListener('click', (e) => {
            e.preventDefault();
            const jsonInput = document.getElementById('jsonFileInput');

            if (!jsonInput || jsonInput.files.length === 0) {
                if (msgError) {
                    msgError.textContent = "Por favor, selecciona un archivo JSON primero.";
                    msgError.classList.remove('hide');
                }
                return;
            }

            const file = jsonInput.files[0];
            const reader = new FileReader();

            reader.onload = async (evento) => {
                try {
                    const payload = JSON.parse(evento.target.result);
                    console.log("JSON leído exitosamente. Enviando a ONNX:", payload);

                    if (msgLoading) msgLoading.classList.remove('hide');
                    if (msgError) msgError.classList.add('hide');

                    const respuesta = await NovaApi.inferenciaManual(payload);
                    if (msgLoading) msgLoading.classList.add('hide');

                    if (respuesta.error) {
                        console.error("Error en inferencia JSON:", respuesta.message);
                        if (msgError) {
                            msgError.textContent = respuesta.message;
                            msgError.classList.remove('hide');
                        }
                    } else {
                        console.log("Respuesta de la IA recibida:", respuesta);
                        respuesta.kepid = payload.kepid || "IA_JSON_TEST";

                        motorManager.inyectarTelemetriaNASA(respuesta);
                        motorManager.reanudarMotor();

                        document.getElementById('valKepid').textContent = respuesta.kepid;
                        document.getElementById('valTemp').textContent = respuesta.temperaturaEstelar || '--';
                        document.getElementById('valGravedad').textContent = respuesta.gravedadEstelar || '--';
                        document.getElementById('valRadio').textContent = respuesta.radioPlanetario || '--';
                        document.getElementById('valDia').textContent = respuesta.duracionOrbital || '--';
                        document.getElementById('valTempEq').textContent = respuesta.temperaturaEquilibrio || '--';

                        actualizarHUDIA(respuesta);
                        if (panelData) panelData.classList.remove('hide');
                    }
                } catch (error) {
                    console.error("Error al procesar el JSON:", error);
                    if (msgError) {
                        msgError.textContent = "El archivo no tiene un formato JSON válido.";
                        msgError.classList.remove('hide');
                    }
                }
            };
            reader.readAsText(file);
        });
    }

    // ==========================================
    // 7. INFERENCIA MANUAL (GRID DE 25 INPUTS)
    // ==========================================
    const variablesRequeridas = [
        "koi_period", "koi_period_err1", "koi_time0bk", "koi_time0bk_err1",
        "koi_impact", "koi_impact_err1", "koi_duration", "koi_duration_err1",
        "koi_depth", "koi_depth_err1", "koi_prad", "koi_prad_err1", "koi_teq",
        "koi_insol", "koi_insol_err1", "koi_model_snr", "koi_steff",
        "koi_steff_err1", "koi_slogg", "koi_slogg_err1", "koi_srad",
        "koi_srad_err1", "ra", "dec", "koi_kepmag"
    ];

    const manualGrid = document.getElementById('manualInputsGrid');
    if (manualGrid) {
        variablesRequeridas.forEach(vari => {
            const inputContainer = document.createElement('div');
            inputContainer.innerHTML = `<input type="number" step="any" id="input_${vari}" placeholder="${vari}" style="width: 100%; background: rgba(0,0,0,0.5); border: 1px solid var(--hud-border); color: var(--hud-cyan); font-size: 0.7rem; padding: 3px;">`;
            manualGrid.appendChild(inputContainer);
        });
    }

    const btnToggleForm = document.getElementById('toggleManualFormBtn');
    if (btnToggleForm) {
        btnToggleForm.addEventListener('click', (e) => {
            e.preventDefault();
            const form = document.getElementById('manualTestForm');
            if (form) form.classList.toggle('hide');
        });
    }

    const btnRunManual = document.getElementById('runManualInferenceBtn');
    if (btnRunManual) {
        btnRunManual.addEventListener('click', async (e) => {
            e.preventDefault();
            console.log("Recolectando datos del formulario...");

            const payload = {};
            variablesRequeridas.forEach(vari => {
                const inputEl = document.getElementById(`input_${vari}`);
                const val = inputEl ? parseFloat(inputEl.value) : 0;
                payload[vari] = isNaN(val) ? 0.0 : val;
            });

            console.log("Enviando Tensor a ONNX:", payload);

            if (msgLoading) msgLoading.classList.remove('hide');
            const respuesta = await NovaApi.inferenciaManual(payload);
            if (msgLoading) msgLoading.classList.add('hide');

            if (respuesta.error) {
                console.error("Error en inferencia:", respuesta.message);
                if (msgError) {
                    msgError.textContent = respuesta.message;
                    msgError.classList.remove('hide');
                }
            } else {
                console.log("Respuesta de la IA recibida:", respuesta);
                respuesta.kepid = "IA_MANUAL_TEST";
                motorManager.inyectarTelemetriaNASA(respuesta);
                motorManager.reanudarMotor();

                document.getElementById('valKepid').textContent = "IA_MANUAL_TEST";
                document.getElementById('valTemp').textContent = respuesta.temperaturaEstelar || '--';
                document.getElementById('valGravedad').textContent = respuesta.gravedadEstelar || '--';
                document.getElementById('valRadio').textContent = respuesta.radioPlanetario || '--';
                document.getElementById('valDia').textContent = respuesta.duracionOrbital || '--';
                document.getElementById('valTempEq').textContent = respuesta.temperaturaEquilibrio || '--';

                actualizarHUDIA(respuesta);
                if (panelData) panelData.classList.remove('hide');
            }
        });
    }

    // ==========================================
    // 8. LIMPIEZA DE MEMORIA (VRAM)
    // ==========================================
    window.addEventListener('unload', () => {
        if (motorManager) motorManager.destruirYApagar();
    });

    console.log('Validaciones de seguridad completadas. Sistema listo.');
});