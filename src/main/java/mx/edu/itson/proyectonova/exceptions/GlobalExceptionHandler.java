package mx.edu.itson.proyectonova.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * Interceptor global que transforma las excepciones de Java en
 * respuestas JSON estandarizadas para el cliente Vanila JS.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Constructor por defecto para el interceptor de excepciones.
     */
    public GlobalExceptionHandler() {
    }

    /**
     * Maneja excepciones de recurso no encontrado y devuelve un error 404 estructurado.
     * @param ex La excepción ResourceNotFoundException capturada.
     * @return Una respuesta HTTP 404 con los detalles del error.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Astro no encontrado en la base de datos: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", true,
                        "status", 404,
                        "message", ex.getMessage()
                ));
    }

    /**
     * Maneja excepciones generales inesperadas y devuelve un error 500 estructurado.
     * @param ex La excepción general capturada.
     * @return Una respuesta HTTP 500 con los detalles del error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralError(Exception ex) {
        log.error("Falla critica en el motor de exploracion: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", true,
                        "status", 500,
                        "message", "El motor de IA colapso inesperadamente. Verifique la integridad del modelo ONNX."
                ));
    }
}