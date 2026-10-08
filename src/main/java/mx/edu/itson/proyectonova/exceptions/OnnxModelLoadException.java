package mx.edu.itson.proyectonova.exceptions;

public class OnnxModelLoadException extends RuntimeException {
  public OnnxModelLoadException(String message, Throwable cause) {
    super(message, cause);
  }
}
