import * as THREE from 'three';

/**
 * Motor de físicas basado en las Leyes de Kepler.
 * Desvincula el movimiento de la tasa de refresco del monitor (Frame-rate independent)
 * y calcula órbitas elípticas reales en lugar de rotaciones circulares simples.
 */
export class AnimacionOrbital {

    /**
     * @param {THREE.Mesh} mallaPlaneta La malla 3D del exoplaneta que se moverá.
     * @param {number} semiejeMayor La distancia máxima al sol (Eje X).
     * @param {number} excentricidad Qué tan ovalada es la órbita (0 = círculo perfecto, >0.1 = elipse).
     */
    constructor(mallaPlaneta, semiejeMayor = 60, excentricidad = 0.2) {
        this.planeta = mallaPlaneta;
        this.semiejeMayor = semiejeMayor;
        this.excentricidad = excentricidad;

        // El reloj maestro de Three.js para calcular el Delta Time (tiempo entre fotogramas)
        this.reloj = new THREE.Clock();

        this.anguloOrbital = 0; // Ángulo inicial (theta)
        this.velocidadTraslacion = 0.5; // Qué tan rápido completa un "año"
        this.velocidadRotacion = 1.5;   // Qué tan rápido completa un "día" (rotación sobre su eje)

        // Cálculo del semieje menor (Eje Z) usando la excentricidad
        this.semiejeMenor = this.semiejeMayor * Math.sqrt(1 - Math.pow(this.excentricidad, 2));
    }

    /**
     * Debe llamarse dentro del requestAnimationFrame.
     * Calcula y aplica las nuevas coordenadas espaciales.
     */
    actualizarFisicas() {
        if (!this.planeta) return;

        // getDelta() devuelve los segundos exactos que pasaron desde el último frame.
        // Si el monitor baja a 30 FPS, el delta sube para compensar la distancia.
        const delta = this.reloj.getDelta();

        // 1. Rotación local (El día del planeta)
        this.planeta.rotation.y += this.velocidadRotacion * delta;

        // 2. Traslación elíptica (El año del planeta)
        this.anguloOrbital += this.velocidadTraslacion * delta;

        // Conversión matemática de coordenadas polares a cartesianas tridimensionales
        const posX = this.semiejeMayor * Math.cos(this.anguloOrbital);
        const posZ = this.semiejeMenor * Math.sin(this.anguloOrbital);

        // Actualizamos la posición real de la malla sin depender de pivotes
        this.planeta.position.set(posX, 0, posZ);
    }
}