package mx.edu.itson.proyectonova.services.impl;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtSession.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.itson.proyectonova.models.entities.KeplerDataEntity;
import mx.edu.itson.proyectonova.utils.TensorBuilderUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Servicio encargado de la inferencia utilizando el motor ONNX.
 */
@Slf4j
@Service
public class OnnxInferenceServiceImpl {

    private final OrtEnvironment environment;
    private final OrtSession session;

    /**
     * Constructor principal para la inyección de dependencias.
     * @param environment Entorno ONNX en la memoria nativa.
     * @param session Sesión activa del modelo neuronal.
     */
    public OnnxInferenceServiceImpl(OrtEnvironment environment, OrtSession session) {
        this.environment = environment;
        this.session = session;
    }

    /**
     * Ejecuta la predicción matemática sobre un registro astrofísico.
     *
     * @param entidad El registro extraído de la base de datos.
     * @return Arreglo de probabilidades [Prob_Falso_Positivo, Prob_Candidato, Prob_Confirmado]
     */
    public float[] predecirClasificacion(KeplerDataEntity entidad) {
        log.info("Iniciando inferencia para el objeto Kepler: {}", entidad.getKepid());

        // 1. Saneamiento y construcción de la matriz [1, 25]
        float[][] tensorJava = TensorBuilderUtil.construirTensor(entidad);

        // Bloque try-with-resources para garantizar que el tensor C++ se libere de RAM instantáneamente
        try (OnnxTensor tensorNativo = OnnxTensor.createTensor(environment, tensorJava)) {

            // 2. Mapeamos el tensor al nombre del input que espera la red neuronal (ej. "input_node")
            // NOTA: Debes verificar cómo se llama tu nodo de entrada en Netron.app
            String inputName = session.getInputNames().iterator().next();
            Map<String, OnnxTensor> inputs = Map.of(inputName, tensorNativo);

            // 3. Ejecución de la propagación hacia adelante (Forward Pass)
            try (Result resultados = session.run(inputs)) {

                // 4. Extracción de los valores de salida. Asumimos que la salida es [1, 3]
                float[][] matrizSalida = (float[][]) resultados.get(0).getValue();

                // Retornamos el vector de probabilidades de la primera (y única) fila
                return matrizSalida[0];
            }

        } catch (Exception e) {
            log.error("Fallo durante la ejecución nativa de ONNX para KEPID: {}", entidad.getKepid(), e);
            throw new RuntimeException("Error en el motor de inferencia IA", e);
        }
    }
}