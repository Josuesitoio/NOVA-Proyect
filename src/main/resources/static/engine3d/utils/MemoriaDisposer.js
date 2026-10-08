import * as THREE from 'three';

/**
 * Utilidad estática de nivel 'Enterprise' para la gestión y limpieza profunda
 * de la memoria gráfica (VRAM) y RAM en Three.js.
 */
export class MemoriaDisposer {

    /**
     * Purgado Completo: Recorre recursivamente una escena o grupo, destruyendo
     * absolutamente todas las geometrías, materiales y texturas asociadas.
     *
     * @param {THREE.WebGLRenderer} renderer El motor de renderizado activo.
     * @param {THREE.Scene|THREE.Group|THREE.Object3D} objetoRaiz El nodo base a limpiar.
     */
    static destruirEscenaCompleta(renderer, objetoRaiz) {
        console.warn("Iniciando purgado profundo de VRAM...");

        if (!objetoRaiz) return;

        // 1. Recorrer la jerarquía de objetos recursivamente
        objetoRaiz.traverse((nodo) => {

            // --- Limpieza de Geometría ---
            if (nodo.geometry) {
                nodo.geometry.dispose(); // Libera los VBOs de la GPU
                // console.log("Geometría liberada:", nodo.geometry.uuid);
            }

            // --- Limpieza de Materiales ---
            if (nodo.material) {
                // En Three.js, un nodo puede tener un material único o una lista de ellos
                if (Array.isArray(nodo.material)) {
                    nodo.material.forEach(mat => this.limpiarMaterialUnique(mat));
                } else {
                    this.limpiarMaterialUnique(nodo.material);
                }
            }
        });

        // 2. Limpieza del Renderer y Vistas
        if (renderer) {
            // Libera el contexto de WebGL y todas las listas internas de renderizado
            renderer.renderLists.dispose();
            renderer.dispose();
            console.warn("Contexto de WebGL del renderer liberado.");
        }

        console.log("Purgado de memoria finalizado. El GC de JS ya puede reclamar la RAM.");
    }

    /**
     * Función interna auxiliar: Se asegura de liberar el material y
     * todas las texturas que pueda tener acopladas (map, bumpMap, etc.).
     *
     * @param {THREE.Material} material El material a destruir.
     */
    static limpiarMaterialUnique(material) {
        if (!material) return;

        // Recorremos todas las propiedades del material buscando texturas
        // (maps, specularMaps, envMaps, shader uniforms, etc.)
        for (const propiedad in material) {
            if (material[propiedad] && material[propiedad].isTexture) {
                material[propiedad].dispose(); // Libera la imagen de la VRAM
                // console.log("Textura liberada:", material[propiedad].uuid);
            }
        }

        // Finalmente, liberamos el programa de shaders del material
        material.dispose();
        // console.log("Material liberado:", material.uuid);
    }
}