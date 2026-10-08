package mx.edu.itson.proyectonova.models.dtos;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;

/**
 * Estructura estandarizada para enviar errores al frontend.
 */
@Data
@Builder
@AllArgsConstructor
public class ApiErrorResponse {

    /**
     * Constructor por defecto para serialización JSON.
     */
    public ApiErrorResponse() {
    }
    private int codigoHttp;
    private String tipoError;
    private String mensaje;
    private LocalDateTime marcaDeTiempo;
}