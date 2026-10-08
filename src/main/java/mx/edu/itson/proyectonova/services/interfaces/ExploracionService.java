package mx.edu.itson.proyectonova.services.interfaces;

import mx.edu.itson.proyectonova.models.dtos.DatosFisicosResponse;
import mx.edu.itson.proyectonova.models.entities.KeplerDataEntity;

/**
 * Contrato para el servicio de exploración de astros.
 * Define las operaciones disponibles para que los controladores las utilicen.
 */
public interface ExploracionService {

    /**
     * Ejecuta el análisis de un exoplaneta buscándolo primero en la base de datos local.
     *
     * @param kepid Identificador oficial de la NASA.
     * @return Propiedades visuales y predicción de la IA.
     */
    DatosFisicosResponse analizarAstro(String kepid);

    /**
     * Ejecuta el análisis de un exoplaneta al vuelo, utilizando datos provistos directamente
     * por el usuario desde la interfaz manual, sin requerir acceso a SQLite.
     *
     * @param entidadTemporario Objeto volátil con las 25 características requeridas por el modelo ONNX.
     * @return Propiedades visuales y predicción de la IA.
     */
    DatosFisicosResponse analizarAstroManual(KeplerDataEntity entidadTemporario);
}