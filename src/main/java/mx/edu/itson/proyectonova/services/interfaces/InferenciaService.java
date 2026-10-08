package mx.edu.itson.proyectonova.services.interfaces;

import mx.edu.itson.proyectonova.models.entities.KeplerDataEntity;

/**
 * Contrato especializado en la ejecución de modelos matemáticos y redes neuronales.
 */
public interface InferenciaService {

    /**
     * Pasa la telemetría a través del modelo neuronal cargado en memoria RAM.
     *
     * @param entidad El registro físico extraído de SQLite.
     * @return Arreglo de coma flotante que representa el vector de probabilidades o logits de salida.
     */
    float[] ejecutarModeloOnnx(KeplerDataEntity entidad);
}