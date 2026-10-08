import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';

/**
 * Gestor avanzado de la cámara espacial.
 * Controla la perspectiva, la interactividad del usuario y la gestión de memoria.
 */
export class CameraManager {

    /**
     * @param {HTMLCanvasElement} canvas El lienzo HTML donde se renderiza la escena.
     */
    constructor(canvas) {
        // 1. Configuración óptica de la Lente
        this.camera = new THREE.PerspectiveCamera(
            45,
            window.innerWidth / window.innerHeight,
            0.1,
            2000
        );
        this.camera.position.set(0, 50, 150); // Vista isométrica inicial

        // 2. Instanciación de los Controles Orbitales
        this.controls = new OrbitControls(this.camera, canvas);

        // 3. Físicas y Restricciones
        this.controls.enableDamping = true;
        this.controls.dampingFactor = 0.05; // Suavidad de la inercia
        this.controls.enablePan = false;    // Bloquea el desplazamiento lateral (fuerza a orbitar)

        // Prevención de colisiones con mallas y límites del universo
        this.controls.minDistance = 30;
        this.controls.maxDistance = 600;
    }

    /**
     * Retorna la instancia activa de la cámara.
     * @returns {THREE.PerspectiveCamera}
     */
    getCamera() {
        return this.camera;
    }

    /**
     * Calcula la inercia frame por frame. Debe ir en el loop de requestAnimationFrame.
     */
    actualizarControles() {
        this.controls.update();
    }

    /**
     * Ajusta la matriz de proyección si el usuario redimensiona la ventana.
     * @param {number} ancho Ancho de la ventana en píxeles.
     * @param {number} alto Alto de la ventana en píxeles.
     */
    redimensionar(ancho, alto) {
        this.camera.aspect = ancho / alto;
        this.camera.updateProjectionMatrix();
    }

    /**
     * Funcionalidad Cinemática: Cambia dinámicamente el punto de interés de la cámara.
     * Ideal para hacer zoom desde la estrella central hacia el exoplaneta.
     *
     * @param {THREE.Vector3} nuevoObjetivo Las coordenadas (x, y, z) a enfocar.
     */
    enfocarEn(nuevoObjetivo) {
        this.controls.target.copy(nuevoObjetivo);
        this.controls.update(); // Fuerza la actualización inmediata del centro de rotación
    }

    /**
     * Prevención de Memory Leaks: Destruye los event listeners del DOM.
     * Crucial si implementas un enrutador (como React Router) y desmontas la vista 3D.
     */
    destruir() {
        if (this.controls) {
            this.controls.dispose();
            console.log("Controles orbitales liberados de la memoria RAM.");
        }
    }
}