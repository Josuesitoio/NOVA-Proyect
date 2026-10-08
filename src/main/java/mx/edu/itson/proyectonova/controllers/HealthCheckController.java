package mx.edu.itson.proyectonova.controllers;

import ai.onnxruntime.OrtSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador de diagnóstico para verificar el estado del servidor y del motor de IA.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {

    // Inyectamos la sesión ONNX para asegurarnos de que la memoria nativa sigue accesible
    private final OrtSession ortSession;

    /**
     * Constructor principal para inyectar dependencias.
     * @param ortSession Sesión ONNX activa para el diagnóstico.
     */
    public HealthCheckController(OrtSession ortSession) {
        this.ortSession = ortSession;
    }

    /**
     * Verifica el estado de salud de la aplicación y del motor de inteligencia artificial.
     * @return Un mapa con el estado del sistema y del motor ONNX, envuelto en una respuesta HTTP 200.
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> checkStatus() {
        log.info("Ejecutando diagnóstico de salud del sistema...");

        boolean isAiEngineAlive = false;
        try {
            // Consultamos un metadato ligero. Si la sesión en RAM está corrupta o cerrada, lanzará una excepción.
            ortSession.getNumInputs();
            isAiEngineAlive = true;
        } catch (Exception e) {
            log.error("El motor ONNX no responde o la sesión fue destruida.", e);
        }

        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "ia_engine", isAiEngineAlive ? "READY" : "DOWN",
                "environment", "Local (Katana Node)"
        ));
    }
}