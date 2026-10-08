package mx.edu.itson.proyectonova.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando un recurso (ej. Astro) no es encontrado en la base de datos.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor de la excepción.
     * @param message El mensaje de error que describe qué no se encontró.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}