package mx.edu.itson.proyectonova.utils;

/**
 * Utilidad matemática estática para procesar los vectores de salida de la red neuronal.
 */
/**
 * Utilidad matemática para calcular la función Softmax sobre arreglos de probabilidades.
 */
public class SoftmaxMathUtil {

    /**
     * Constructor privado para ocultar el implícito público y prevenir instanciación.
     */
    private SoftmaxMathUtil() {
    }

    /**
     * Aplica la función Softmax a un arreglo de logits crudos.
     * Convierte valores numéricos arbitrarios en una distribución de probabilidad,
     * garantizando que la suma de todos los elementos sea exactamente 1.0 (100%).
     *
     * @param logits Arreglo de números reales arrojados por el modelo ONNX.
     * @return Arreglo con los porcentajes de probabilidad (en precisión double) listos para la interfaz gráfica.
     */
    public static double[] calcularDistribucion(float[] logits) {
        // Validación de seguridad defensiva
        if (logits == null || logits.length == 0) {
            return new double[0];
        }

        // 1. Encontrar el logit máximo para estabilidad numérica (Max Trick)
        double maxLogit = Double.NEGATIVE_INFINITY;
        for (float logit : logits) {
            if (logit > maxLogit) {
                maxLogit = logit;
            }
        }

        double sumExp = 0.0;
        double[] exponentials = new double[logits.length];

        // 2. Calcular la exponencial de cada elemento y acumular la suma
        for (int i = 0; i < logits.length; i++) {
            exponentials[i] = Math.exp(logits[i] - maxLogit);
            sumExp += exponentials[i];
        }

        double[] probabilidades = new double[logits.length];

        // 3. Normalizar dividiendo entre la suma total
        for (int i = 0; i < probabilidades.length; i++) {
            probabilidades[i] = exponentials[i] / sumExp;
        }

        return probabilidades;
    }
}