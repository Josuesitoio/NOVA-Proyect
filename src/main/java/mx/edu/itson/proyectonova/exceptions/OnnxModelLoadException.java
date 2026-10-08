package mx.edu.itson.proyectonova.exceptions;

/**
 * Excepción lanzada cuando hay un error crítico al cargar el modelo neuronal ONNX.
 */
public class OnnxModelLoadException extends RuntimeException {

    /**
     * Constructor de la excepción.
     * @param message El mensaje de error detallado.
     * @param cause La causa del error.
     */
  public OnnxModelLoadException(String message, Throwable cause) {
    super(message, cause);
  }
}
