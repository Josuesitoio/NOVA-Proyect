/**
 * Shaders personalizados (GLSL) para simular atmósferas volumétricas y dispersión de luz.
 * Este código se ejecuta directamente en los núcleos de la GPU (Tarjeta Gráfica), no en el procesador.
 */
export const AtmosferaShaders = {

    /**
     * VERTEX SHADER
     * Se ejecuta una vez por cada vértice de la esfera.
     * Su trabajo es calcular la posición matemática tridimensional y preparar las normales.
     */
    vertexShader: `
        // 'varying' declara una variable que viajará hacia el Fragment Shader
        varying vec3 vNormal;
        
        void main() {
            // Calculamos hacia dónde apunta este vértice específico (su vector normal)
            vNormal = normalize(normalMatrix * normal);
            
            // Proyección matricial estándar de WebGL para ubicar el punto en el monitor
            gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
        }
    `,

    /**
     * FRAGMENT SHADER
     * Se ejecuta una vez por cada píxel visible en la pantalla.
     * Aquí calculamos el Efecto Fresnel: la luz rebota diferente en los bordes de una esfera que en su centro.
     */
    fragmentShader: `
        // 'uniform' recibe variables dinámicas inyectadas desde JavaScript (Three.js)
        uniform vec3 colorAtmico;
        
        // Recibimos la normal que calculó el Vertex Shader
        varying vec3 vNormal;
        
        void main() {
            // Cálculo del Efecto Fresnel
            // Comparamos hacia dónde apunta el píxel (vNormal) vs la cámara (0, 0, 1)
            // Si el píxel está en el borde, el producto punto es cercano a 0, aumentando la intensidad.
            float intensidad = pow(0.65 - dot(vNormal, vec3(0.0, 0.0, 1.0)), 4.0);
            
            // Pintamos el píxel multiplicando el color inyectado por la opacidad (intensidad)
            gl_FragColor = vec4(colorAtmico, 1.0) * intensidad;
        }
    `
};