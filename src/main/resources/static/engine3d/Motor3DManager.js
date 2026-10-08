import * as THREE from 'three';
// Rutas corregidas apuntando a los subdirectorios reales
import { EscenaBuilder } from './core/EscenaBuilder.js';
import { CameraManager } from './core/CameraManager.js';
import { LightingManager } from './core/LightingManager.js';
import { RenderEstrella } from './entities/RenderEstrella.js';
import { RenderExoplaneta } from './entities/RenderExoplaneta.js';
import { MemoriaDisposer } from './utils/MemoriaDisposer.js';
import { ColorimetriaAstrofisica } from './utils/ColorimetriaAstrofisica.js';

/**
 * Orquestador principal del motor gráfico tridimensional.
 * Encapsula el ciclo de renderizado (Game Loop), la gestión de memoria VRAM
 * y la inyección de telemetría de la NASA proveniente del backend.
 */
export class Motor3DManager {

    /**
     * Inicializa todo el entorno 3D.
     * @param {HTMLCanvasElement} canvasElement El elemento <canvas> de la interfaz web.
     */
    constructor(canvasElement) {
        if (!canvasElement) {
            throw new Error("Motor3DManager: Se requiere un elemento Canvas válido.");
        }

        console.log("Inicializando Motor 3D Nova... (Renderizado AAA Activado)");

        // 1. Configuración del Renderer (WebGL2 con optimizaciones de hardware)
        this.renderer = new THREE.WebGLRenderer({
            canvas: canvasElement,
            antialias: true, // Suavizado de bordes
            alpha: false,     // Fondo opaco para máximo rendimiento
            powerPreference: "high-performance" // Sugiere usar la GPU dedicada (RTX 4060)
        });

        // Optimización para pantallas de alta densidad (Retina/4K) sin saturar la GPU
        this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
        this.renderer.setSize(window.innerWidth, window.innerHeight);

        // Post-procesado cinemático integrado (ACES Filmic Tone Mapping)
        // Esto le da el aspecto de "película" y evita que las estrellas se vean como simples círculos blancos
        this.renderer.toneMapping = THREE.ACESFilmicToneMapping;
        this.renderer.toneMappingExposure = 1.2;

        // 2. Inicialización de Subsistemas Fundamentales
        this.escena = EscenaBuilder.crearEscena();
        this.camaraManager = new CameraManager(canvasElement);
        this.lucesManager = new LightingManager(this.escena);

        // 3. Instanciación de Actores Tridimensionales
        this.estrella = new RenderEstrella(this.escena);
        this.exoplaneta = new RenderExoplaneta(this.escena);

        // 4. Control del Ciclo de Vida y Eventos
        this.animacionID = null;
        this.isRunning = false;

        // Manejo correcto del 'this' en eventos del DOM
        this.redimensionarBinder = this.alRedimensionar.bind(this);
        window.addEventListener('resize', this.redimensionarBinder);

        // 5. Arranque del Motor
        this.iniciarCicloRender();
    }

    /**
     * El "Game Loop" principal. Se ejecuta en sincronía con el refresco del monitor.
     * Coordina las actualizaciones físicas y el dibujado del fotograma.
     */
    iniciarCicloRender() {
        if (this.isRunning) return;
        this.isRunning = true;

        const loop = () => {
            if (!this.isRunning) return;

            // A. Actualizar subsistemas e interactividad
            this.camaraManager.actualizarControles();

            // B. Actualizar físicas y animaciones de Shaders (Independientes del frame-rate)
            this.estrella.animarRotacion();
            this.exoplaneta.animar();

            // C. Renderizar la escena desde la perspectiva de la cámara activa
            this.renderer.render(this.escena, this.camaraManager.getCamera());

            // D. Solicitar el siguiente fotograma
            this.animacionID = requestAnimationFrame(loop);
        };

        loop();
    }

    /**
     * Detiene el renderizado sin destruir los objetos. Útil para pausar la simulación.
     */
    pausarMotor() {
        this.isRunning = false;
        if (this.animacionID) {
            cancelAnimationFrame(this.animacionID);
        }
        console.log("Motor 3D pausado.");
    }

    /**
     * Reanuda el renderizado si estaba pausado.
     */
    reanudarMotor() {
        if (!this.isRunning) {
            this.iniciarCicloRender();
            console.log("Motor 3D reanudado.");
        }
    }

    /**
     * Recibe los datos físicos crudos del backend (Spring Boot) y orquesta
     * la actualización visual de todo el sistema estelar.
     *
     * @param {Object} datosTelemetria DTO DatosFisicosResponse proveniente de la API.
     */
    inyectarTelemetriaNASA(datosTelemetria) {
        if (!datosTelemetria) {
            console.warn("Motor3DManager: Se recibieron datos de telemetría nulos.");
            return;
        }

        console.log("Inyectando telemetría de la NASA para:", datosTelemetria.kepid || "Exoplaneta");

        // 1. Actualizar morfología y Shaders de los astros
        this.estrella.actualizarTelemetria(datosTelemetria);
        this.exoplaneta.actualizarTelemetria(datosTelemetria);

        // 2. Sincronizar la iluminación ambiental con el color real de la estrella (Kelvin)
        if (datosTelemetria.temperaturaEstelar) {
            const colorEstelarHex = ColorimetriaAstrofisica.kelvinToHex(datosTelemetria.temperaturaEstelar);
            this.lucesManager.actualizarPropiedadesEstelares(colorEstelarHex);
        }

        // 3. Reset cinemático de cámara: Enfocar al centro del nuevo sistema
        this.camaraManager.enfocarEn(new THREE.Vector3(0, 0, 0));
    }

    /**
     * Ajusta el viewport del renderer y la matriz de la cámara si cambia el tamaño de la ventana.
     */
    alRedimensionar() {
        const ancho = window.innerWidth;
        const alto = window.innerHeight;

        this.renderer.setSize(ancho, alto);
        this.camaraManager.redimensionar(ancho, alto);
    }

    /**
     * APAGADO SEGURO (Deep Dispose): Libera absolutamente toda la memoria RAM y VRAM.
     * Esencial para evitar Memory Leaks en aplicaciones de larga duración (React, SPAs).
     */
    destruirYApagar() {
        console.warn("Apagando Motor 3D y liberando VRAM...");

        // 1. Detener eventos del DOM y el bucle de renderizado
        this.isRunning = false;
        window.removeEventListener('resize', this.redimensionarBinder);
        if (this.animacionID) {
            cancelAnimationFrame(this.animacionID);
        }

        // 2. Delegar limpieza de managers individuales (controles, luces, etc.)
        this.camaraManager.destruir();
        // this.lucesManager.destruir(); // Si implementaste disposición interna

        // 3. EJECUTAR PURGADO PROFUNDO DE LA ESCENA (MemoriaDisposer)
        // Esto elimina geometrías, materiales y texturas crudas de la GPU
        MemoriaDisposer.destruirEscenaCompleta(this.renderer, this.escena);

        // 4. Limpieza de referencias locales para el Garbage Collector de JS
        this.renderer = null;
        this.escena = null;
        this.camaraManager = null;
        this.lucesManager = null;
        this.estrella = null;
        this.exoplaneta = null;

        console.log("Motor  Nova apagado y VRAM purgada limpiamente.");
    }
}