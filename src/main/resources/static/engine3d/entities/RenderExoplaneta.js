import * as THREE from 'three';
import { AnimacionOrbital } from './AnimacionOrbital.js';
// CORRECCIÓN: Subimos un nivel de directorio y entramos a 'shaders'
import { AtmosferaShaders } from '../shaders/Atmosphere.glsl.js';// <-- Importación añadida

/**
 * Entidad avanzada encargada de generar la malla física, la atmósfera volumétrica
 * y delegar el movimiento real a las físicas de Kepler (AnimacionOrbital).
 */
export class RenderExoplaneta {

    constructor(escena) {
        this.escena = escena;

        // Parámetros orbitales base
        this.distanciaOrbital = 60; // Semieje mayor
        this.excentricidad = 0.2;   // Nivel de óvalo

        // 1. Geometría Base
        this.geometria = new THREE.SphereGeometry(1, 64, 64);

        // 2. Material Físico Ultra-Realista (PBR)
        this.material = new THREE.MeshPhysicalMaterial({
            color: 0x888888,
            roughness: 0.8,
            metalness: 0.2,
            bumpMap: this.generarTexturaProcedural(),
            bumpScale: 0.05,
            clearcoat: 0.0,
        });

        // 3. Material de Atmósfera (Efecto Fresnel)
        this.materialAtmosfera = new THREE.ShaderMaterial({
            uniforms: { colorAtmico: { value: new THREE.Color(0x00aaff) } },
            vertexShader: AtmosferaShaders.vertexShader,     // <-- Conectado al archivo GLSL
            fragmentShader: AtmosferaShaders.fragmentShader, // <-- Conectado al archivo GLSL
            blending: THREE.AdditiveBlending,
            side: THREE.BackSide,
            transparent: true,
            depthWrite: false
        });

        this.construirSistemaOrbital();
    }

    /**
     * Mapa procedural (Ruido Matemático) para montañas y cráteres.
     */
    generarTexturaProcedural() {
        const canvas = document.createElement('canvas');
        canvas.width = 512;
        canvas.height = 512;
        const ctx = canvas.getContext('2d');

        for (let x = 0; x < canvas.width; x++) {
            for (let y = 0; y < canvas.height; y++) {
                const valorRuido = (Math.sin(x * 0.05) + Math.cos(y * 0.05) + Math.sin((x + y) * 0.02)) * 80;
                const color = Math.floor(128 + valorRuido);
                ctx.fillStyle = `rgb(${color},${color},${color})`;
                ctx.fillRect(x, y, 1, 1);
            }
        }
        const textura = new THREE.CanvasTexture(canvas);
        textura.anisotropy = 16;
        return textura;
    }

    /**
     * Ensambla las mallas físicas y delega el movimiento al motor independiente.
     */
    construirSistemaOrbital() {
        // A. Malla Principal (Directamente a la escena, sin pivotes falsos)
        this.malla = new THREE.Mesh(this.geometria, this.material);
        this.escena.add(this.malla);

        // B. Capa Atmosférica (Anclada al planeta)
        this.mallaAtmosfera = new THREE.Mesh(this.geometria, this.materialAtmosfera);
        this.mallaAtmosfera.scale.set(1.15, 1.15, 1.15);
        this.malla.add(this.mallaAtmosfera);

        // C. Trazado de Órbita Elíptica Exacta
        const semiejeMenor = this.distanciaOrbital * Math.sqrt(1 - Math.pow(this.excentricidad, 2));
        const curvaElipse = new THREE.EllipseCurve(
            0, 0,                             // Centro
            this.distanciaOrbital, semiejeMenor, // xRadius, yRadius
            0, 2 * Math.PI,                   // Rotación completa
            false, 0                          // Anti-horario
        );
        const puntos = curvaElipse.getPoints(128);
        const geometriaCurva = new THREE.BufferGeometry().setFromPoints(puntos);
        const materialCurva = new THREE.LineBasicMaterial({ color: 0xffffff, transparent: true, opacity: 0.15 });

        this.anilloOrbital = new THREE.Line(geometriaCurva, materialCurva);
        this.anilloOrbital.rotation.x = Math.PI / 2; // Acostar la elipse horizontalmente
        this.escena.add(this.anilloOrbital);

        // D. Inyección del motor físico (Reemplaza al pivoteOrbital)
        this.fisicas = new AnimacionOrbital(this.malla, this.distanciaOrbital, this.excentricidad);
    }

    /**
     * Actualiza el bioma al recibir los datos.
     */
    actualizarTelemetria(datosFisicos) {
        if (!datosFisicos) return;

        const escalaRadio = datosFisicos.radioPlanetario ? (datosFisicos.radioPlanetario * 0.5) : 2;
        this.malla.scale.set(escalaRadio, escalaRadio, escalaRadio);

        const temp = datosFisicos.temperaturaEquilibrio;

        this.material.emissive.setHex(0x000000);
        this.material.clearcoat = 0.0;

        if (temp) {
            if (temp < 200) {
                this.material.color.setHex(0xd4ebf2);
                this.material.clearcoat = 1.0;
                this.material.roughness = 0.3;
                this.materialAtmosfera.uniforms.colorAtmico.value.setHex(0xffffff);
            } else if (temp >= 200 && temp <= 350) {
                this.material.color.setHex(0x2d6a4f);
                this.material.clearcoat = 0.4;
                this.materialAtmosfera.uniforms.colorAtmico.value.setHex(0x0077ff);
            } else if (temp > 350 && temp <= 800) {
                this.material.color.setHex(0xbc7639);
                this.material.roughness = 1.0;
                this.materialAtmosfera.uniforms.colorAtmico.value.setHex(0xd4a373);
            } else {
                this.material.color.setHex(0x220000);
                this.material.emissive.setHex(0x550000);
                this.material.roughness = 0.9;
                this.materialAtmosfera.uniforms.colorAtmico.value.setHex(0xff3300);
            }
        }
    }

    /**
     * El cálculo frame a frame se delega por completo a las matemáticas cartesianas.
     */
    animar() {
        if (this.fisicas) {
            this.fisicas.actualizarFisicas();
        }
    }

    /**
     * Limpieza profunda de memoria.
     */
    destruir() {
        if (this.malla) {
            this.escena.remove(this.malla);
            this.escena.remove(this.anilloOrbital);

            this.geometria.dispose();
            this.material.dispose();
            this.materialAtmosfera.dispose();

            this.anilloOrbital.geometry.dispose();
            this.anilloOrbital.material.dispose();

            if (this.material.bumpMap) this.material.bumpMap.dispose();

            console.log("Sistema exoplanetario purgado limpiamente (sin pivotes).");
        }
    }
}