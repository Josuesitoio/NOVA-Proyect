package mx.edu.itson.proyectonova.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que contiene las propiedades visuales y la predicción para el cliente.
 */
@Data
@Builder
@AllArgsConstructor
public class DatosFisicosResponse {

    /**
     * Constructor por defecto.
     */
    public DatosFisicosResponse() {
    }

    // Propiedades físicas para el motor 3D
    private String kepid;
    private Double temperaturaEstelar;
    private Double gravedadEstelar;
    private Double radioPlanetario;
    private Double duracionOrbital;
    private Double temperaturaEquilibrio;
    private Double radioEstelar;

    // Métricas de Inteligencia Artificial
    private Double probabilidadExoplaneta;
    private Boolean confirmadoPorIA;
}