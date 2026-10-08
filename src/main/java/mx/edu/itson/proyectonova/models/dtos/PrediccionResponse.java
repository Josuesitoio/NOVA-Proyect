package mx.edu.itson.proyectonova.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de Transferencia de Datos (DTO) para enviar el resultado de la inferencia al frontend.
 */
@Data
@Builder
@AllArgsConstructor
public class PrediccionResponse {

    /**
     * Constructor por defecto requerido para la serialización.
     */
    public PrediccionResponse() {
    }

    private String kepid;
    private String estadoOriginalNasa;

    // Vector Softmax desglosado
    private float probabilidadFalsoPositivo;
    private float probabilidadCandidato;
    private float probabilidadConfirmado;

    // La conclusión final de la red neuronal (el valor más alto)
    private String clasificacionRedNeuronal;
}