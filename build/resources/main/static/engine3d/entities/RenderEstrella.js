import * as THREE from 'three';
import { ColorimetriaAstrofisica } from '../utils/ColorimetriaAstrofisica.js';
// CORRECCIÓN: Subimos un nivel de directorio y entramos a 'shaders'
import { CoronaShaders } from '../shaders/StarCorona.glsl.js';

/**
 * Entidad encargada de generar y administrar la malla 3D de la estrella central
 * y su simulación termodinámica de plasma y radiación.
 */
export class RenderEstrella {

    /**
     * @param {THREE.Scene} escena La escena principal de WebGL.
     */
    constructor(escena) {
        this.escena = escena;

        // 1. Geometría compartida para el núcleo y la corona
        this.geometria = new THREE.SphereGeometry(1, 64, 64);

        // 2. Material del núcleo incandescente
        this.materialBase = new THREE.MeshBasicMaterial({
            color: 0xffffff
        });

        // 3. Material volumétrico de la Corona Estelar (Custom GLSL)
        this.materialCorona = new THREE.ShaderMaterial({
            uniforms: {
                colorEstelar: { value: new THREE.Color(0xffffff) },
                tiempo: { value: 0.0 },
                volatilidad: { value: 1.0 } // Se calculará según la gravedad
            },
            vertexShader: CoronaShaders.vertexShader,
            fragmentShader: CoronaShaders.fragmentShader,
            blending: THREE.AdditiveBlending, // Fusión lumínica intensa
            side: THREE.BackSide,
            transparent: true,
            depthWrite: false
        });

        this.construirSistemaEstelar();
    }

    /**
     * Ensambla el núcleo y la corona en una sola entidad agrupada.
     */
    construirSistemaEstelar() {
        // Usamos un Grupo para escalar ambas mallas simultáneamente
        this.grupoEstelar = new THREE.Group();
        this.escena.add(this.grupoEstelar);

        // A. Malla del Núcleo Sólido
        this.mallaBase = new THREE.Mesh(this.geometria, this.materialBase);
        this.grupoEstelar.add(this.mallaBase);

        // B. Malla de la Corona (40% más grande que el núcleo para dejar espacio al halo)
        this.mallaCorona = new THREE.Mesh(this.geometria, this.materialCorona);
        this.mallaCorona.scale.set(1.4, 1.4, 1.4);
        this.grupoEstelar.add(this.mallaCorona);
    }

    /**
     * Traduce los parámetros astrofísicos del backend de Java a visuales en WebGL.
     *
     * @param {Object} datosFisicos DTO de Java (DatosFisicosResponse).
     */
    actualizarTelemetria(datosFisicos) {
        if (!datosFisicos) return;

        // 1. Escalar el sistema completo según el radio estelar (koi_srad)
        const escalaRadio = datosFisicos.radioEstelar ? datosFisicos.radioEstelar * 15 : 15;
        this.grupoEstelar.scale.set(escalaRadio, escalaRadio, escalaRadio);

        // 2. Colorear núcleo y plasma según la Temperatura de Kelvin (koi_steff)
        if (datosFisicos.temperaturaEstelar) {
            const hexColor = ColorimetriaAstrofisica.kelvinToHex(datosFisicos.temperaturaEstelar);
            this.materialBase.color.setHex(hexColor);
            this.materialCorona.uniforms.colorEstelar.value.setHex(hexColor);
        }

        // 3. Simular turbulencia plasmática usando la Gravedad Superficial (koi_slogg)
        // A menor gravedad (estrellas gigantes), la corona es más inestable y volátil.
        if (datosFisicos.gravedadEstelar) {
            // El Sol tiene un log(g) de ~4.4. Usamos matemáticas para invertir la relación visual.
            const gravedad = Math.max(datosFisicos.gravedadEstelar, 1.0);
            this.materialCorona.uniforms.volatilidad.value = 6.0 / gravedad;
        }
    }

    /**
     * Debe invocarse dentro del Game Loop (requestAnimationFrame).
     */
    animarRotacion() {
        if (this.grupoEstelar) {
            // Rotación mecánica del astro completo
            this.grupoEstelar.rotation.y += 0.002;

            // Animación termodinámica de los Shaders de la GPU
            this.materialCorona.uniforms.tiempo.value += 0.01;
        }
    }

    /**
     * Liberación estricta de memoria para prevenir Memory Leaks en la RTX 4060.
     */
    destruir() {
        if (this.grupoEstelar) {
            this.grupoEstelar.remove(this.mallaBase);
            this.grupoEstelar.remove(this.mallaCorona);
            this.escena.remove(this.grupoEstelar);

            this.geometria.dispose();
            this.materialBase.dispose();
            this.materialCorona.dispose();

            console.log("Sistema estelar central purgado exitosamente de VRAM.");
        }
    }
}