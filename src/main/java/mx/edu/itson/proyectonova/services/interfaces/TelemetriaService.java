package mx.edu.itson.proyectonova.services.interfaces;

import mx.edu.itson.proyectonova.models.dtos.DatosFisicosResponse;

/**
 * Contrato para el manejo de las propiedades físicas y visuales del sistema estelar.
 */
public interface TelemetriaService {

    /**
     * Obtiene los parámetros fundamentales necesarios para construir y colorear las mallas 3D.
     *
     * @param kepid El identificador oficial del catálogo Kepler de la NASA.
     * @return El objeto de transferencia con los datos físicos para el frontend.
     */
    DatosFisicosResponse obtenerPropiedadesVisuales(String kepid);
}