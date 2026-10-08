import * as THREE from 'three';

/**
 * Arquitecto principal del universo tridimensional.
 */
export class EscenaBuilder {

    /**
     * Inicializa y configura el objeto Scene de WebGL.
     * @returns {THREE.Scene} La escena base configurada.
     */
    static crearEscena() {
        const escena = new THREE.Scene();

        // 1. Configurar el color de fondo del vacío espacial (Negro absoluto)
        escena.background = new THREE.Color(0x000000);

        // 2. Niebla estelar (Fog)
        // Añade percepción de escala. Los objetos lejanos se desvanecerán gradualmente
        // en la oscuridad en lugar de cortarse abruptamente en el horizonte del render.
        escena.fog = new THREE.FogExp2(0x000000, 0.0015);

        return escena;
    }
}