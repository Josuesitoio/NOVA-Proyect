/**
 * Shaders personalizados (GLSL) para simular la corona estelar,
 * erupciones solares y el halo de radiación de la estrella central.
 */
export const CoronaShaders = {

    /**
     * VERTEX SHADER
     * Prepara la geometría y expone la posición 3D local para que el Fragment Shader
     * pueda calcular el ruido y la turbulencia espacial.
     */
    vertexShader: `
        varying vec3 vNormal;
        varying vec3 vPosition;
        
        void main() {
            // Normalizamos el vector para saber la curvatura de la esfera
            vNormal = normalize(normalMatrix * normal);
            // Guardamos la posición original antes de la proyección a la pantalla
            vPosition = position;
            
            gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
        }
    `,

    /**
     * FRAGMENT SHADER
     * Calcula la incandescencia y la pulsación del plasma fotograma a fotograma.
     */
    fragmentShader: `
        // Variables dinámicas que inyectaremos desde JavaScript
        uniform vec3 colorEstelar;
        uniform float tiempo;
        uniform float volatilidad; // Se alimenta de la gravedad estelar (koi_slogg)
        
        varying vec3 vNormal;
        varying vec3 vPosition;
        
        void main() {
            // 1. Efecto Fresnel Invertido (Halo exterior)
            // Hace que el centro sea transparente y los bordes brillen con intensidad
            float halo = pow(0.6 - dot(vNormal, vec3(0.0, 0.0, 1.0)), 3.0);
            
            // 2. Turbulencia y pulsación matemática
            // Usamos el tiempo y la posición para crear variaciones asimétricas de luz
            float palpitacion = sin(tiempo * volatilidad + vPosition.y * 2.0) * 0.15 + 0.85;
            
            // 3. Ensamblaje del color final
            vec3 colorFinal = colorEstelar * halo * palpitacion;
            
            // 4. Canal Alpha (Transparencia)
            // Se desvanece suavemente hacia el vacío del espacio
            float transparencia = halo * 2.0;
            
            gl_FragColor = vec4(colorFinal, transparencia);
        }
    `
};