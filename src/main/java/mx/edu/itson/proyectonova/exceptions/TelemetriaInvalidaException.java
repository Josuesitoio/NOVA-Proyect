package mx.edu.itson.proyectonova.exceptions;

/**
 * Excepción lanzada cuando la telemetría recibida es inválida o está incompleta.
 */
public class TelemetriaInvalidaException extends RuntimeException {

    /**
     * Constructor de la excepción.
     * @param message El mensaje de error que explica la validación fallida.
     */
    public TelemetriaInvalidaException(String message) {
        super(message);
    }
}
