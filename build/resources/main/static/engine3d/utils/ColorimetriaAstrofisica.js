/**
 * Herramienta estática para cálculos fotométricos y visuales.
 */
export class ColorimetriaAstrofisica {

    /**
     * Algoritmo de radiación de cuerpo negro (Blackbody Radiation).
     * Transforma una temperatura estelar (Ej. 5778K del Sol) en un color hexadecimal de Three.js.
     *
     * @param {number} kelvin La temperatura efectiva estelar (koi_steff).
     * @returns {number} Valor hexadecimal (ej. 0xffffff) listo para inyectarse en el material 3D.
     */
    static kelvinToHex(kelvin) {
        if (!kelvin) return 0xffffff; // Blanco por defecto si falta la telemetría

        // Rango de seguridad: el algoritmo original de Tanner Helland funciona entre 1000K y 40000K
        const clampKelvin = Math.min(Math.max(kelvin, 1000), 40000);
        let temp = clampKelvin / 100;
        let red, green, blue;

        // Cálculo de aproximación física
        if (temp <= 66) {
            red = 255;
            green = 99.4708025861 * Math.log(temp) - 161.1195681661;
            blue = temp <= 19 ? 0 : 138.5177312231 * Math.log(temp - 10) - 305.0447927307;
        } else {
            red = 329.698727446 * Math.pow(temp - 60, -0.1332047592);
            green = 286.2229231441 * Math.pow(temp - 60, -0.0755148492);
            blue = 255;
        }

        // Función auxiliar para mantener los canales RGB dentro del límite 0-255
        const clampColor = (x) => Math.min(Math.max(Math.round(x), 0), 255);

        // Retorna el formato numérico bit a bit (0xRRGGBB) que exige Three.js
        return (clampColor(red) << 16) | (clampColor(green) << 8) | clampColor(blue);
    }
}