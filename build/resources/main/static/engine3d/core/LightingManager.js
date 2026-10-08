import * as THREE from 'three';

/**
 * Gestor avanzado de iluminación física para la escena espacial.
 * Separa la lógica de los focos lumínicos del renderizado principal.
 */
export class LightingManager {

    /**
     * @param {THREE.Scene} escena La escena principal donde se inyectarán las luces.
     */
    constructor(escena) {
        this.escena = escena;

        // 1. Luz Ambiental Base (Starlight)
        // Evita que la cara oscura del exoplaneta sea 100% negra, simulando
        // el ligerísimo rebote de luz de la vía láctea de fondo.
        this.luzAmbiental = new THREE.AmbientLight(0xffffff, 0.05);
        this.escena.add(this.luzAmbiental);

        // 2. Luz Principal de la Estrella Central (PointLight)
        // Foco omnidireccional que nace desde el centro del sistema solar (0,0,0)
        // y proyecta luz sobre los exoplanetas que orbitan a su alrededor.
        this.luzEstelar = new THREE.PointLight(0xffffff, 2.5, 2000);
        this.luzEstelar.position.set(0, 0, 0); // Centro exacto del universo local
        this.escena.add(this.luzEstelar);

        // 3. Luz Direccional de Relleno (opcional para resaltar detalles 3D)
        this.luzDireccional = new THREE.DirectionalLight(0xffffff, 0.5);
        this.luzDireccional.position.set(50, 50, 50);
        this.escena.add(this.luzDireccional);
    }

    /**
     * Ajusta dinámicamente la irradiación del sistema basándose en la telemetría del backend.
     *
     * @param {number} colorHexadecimal El color derivado de la variable koi_steff (Temperatura).
     * @param {number} intensidad Múltiplo de fuerza lumínica basada en el radio estelar (koi_srad).
     */
    actualizarPropiedadesEstelares(colorHexadecimal, intensidad = 2.5) {
        this.luzEstelar.color.setHex(colorHexadecimal);
        this.luzEstelar.intensity = intensidad;

        // La luz direccional acompaña sutilmente el tono de la estrella principal
        this.luzDireccional.color.setHex(colorHexadecimal);
    }

    /**
     * Prevención de Memory Leaks: Libera las luces si la escena se destruye.
     */
    destruir() {
        this.escena.remove(this.luzAmbiental);
        this.escena.remove(this.luzEstelar);
        this.escena.remove(this.luzDireccional);

        this.luzAmbiental.dispose();
        this.luzEstelar.dispose();
        this.luzDireccional.dispose();

        console.log("Iluminación liberada de la memoria VRAM.");
    }
}