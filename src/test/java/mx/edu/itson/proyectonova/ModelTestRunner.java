package mx.edu.itson.proyectonova;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.FloatBuffer;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba unitaria y de integración para validar el motor de Inteligencia Artificial (ONNX).
 *
 * Propósito arquitectónico:
 * Utiliza JUnit 5 y el contexto real de Spring Boot para verificar que el modelo
 * NOVA.onnx está correctamente alojado en RAM y es capaz de procesar un tensor
 * de 25 variables flotantes devolviendo un vector de probabilidad válido.
 */
@SpringBootTest // Levanta el contenedor de Spring Boot para inyectar los beans reales (OnnxConfig)
class OnnxModelTest {

    @Autowired
    private OrtEnvironment ortEnvironment;

    @Autowired
    private OrtSession ortSession;

    @Test
    @DisplayName("Debe cargar el modelo ONNX en RAM exitosamente desde Spring Boot")
    void testModelIsLoaded() {
        assertNotNull(ortEnvironment, "El entorno de ONNX no debería ser nulo");
        assertNotNull(ortSession, "La sesión del modelo ONNX debería estar inicializada en memoria");
    }

    @Test
    @DisplayName("Debe ejecutar una inferencia exitosa con un tensor sintético de 25 variables")
    void testSuccessfulInference() throws Exception {
        // 1. Preparamos un arreglo de prueba con 25 flotantes (simulando telemetría de exoplaneta)
        float[] sampleInput = new float[25];
        for (int i = 0; i < sampleInput.length; i++) {
            sampleInput[i] = 0.5f; // Valores sintéticos controlados
        }

        // 2. Identificamos el nombre del nodo de entrada que espera el modelo
        String inputName = ortSession.getInputNames().iterator().next();
        assertNotNull(inputName, "El modelo debe tener un nodo de entrada definido");

        // 3. Construimos el tensor de entrada con dimensiones [1, 25]
        long[] shape = {1, 25};
        FloatBuffer floatBuffer = FloatBuffer.wrap(sampleInput);

        try (OnnxTensor tensor = OnnxTensor.createTensor(ortEnvironment, floatBuffer, shape)) {

            Map<String, OnnxTensor> inputs = Map.of(inputName, tensor);

            // 4. Ejecutamos la predicción contra el modelo en RAM
            try (OrtSession.Result result = ortSession.run(inputs)) {

                String outputName = ortSession.getOutputNames().iterator().next();
                assertNotNull(outputName, "El modelo debe tener un nodo de salida definido");

                // 5. Extraemos el resultado y validamos que la IA escupió datos
                float[][] outputData = (float[][]) result.get(outputName).get().getValue();

                assertNotNull(outputData, "La salida de la inferencia no debe ser nula");
                assertTrue(outputData.length > 0, "Debe retornar al menos una fila de predicción");
                assertTrue(outputData[0].length > 0, "El vector de probabilidades softmax debe contener elementos");
            }
        }
    }
}