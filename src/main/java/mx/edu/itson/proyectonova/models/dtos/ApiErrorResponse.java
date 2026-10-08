package mx.edu.itson.proyectonova.models.dtos;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Estructura estandarizada para enviar errores al frontend.
 */
@Data
@Builder
public class ApiErrorResponse {
    private int codigoHttp;
    private String tipoError;
    private String mensaje;
    private LocalDateTime marcaDeTiempo;
}