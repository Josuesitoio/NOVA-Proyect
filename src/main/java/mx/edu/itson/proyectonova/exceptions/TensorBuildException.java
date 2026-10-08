package mx.edu.itson.proyectonova.exceptions;

/**
 * Excepción lanzada cuando ocurre un error al intentar construir el tensor para ONNX.
 */
public class TensorBuildException extends RuntimeException {
    
    /**
     * Constructor de la excepción.
     * @param message El mensaje de error.
     * @param cause La causa original del error.
     */
    public TensorBuildException(String message, Throwable cause) {
        super(message, cause);
    }
}
