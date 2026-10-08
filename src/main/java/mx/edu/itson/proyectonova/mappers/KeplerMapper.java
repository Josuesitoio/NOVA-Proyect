package mx.edu.itson.proyectonova.mappers;

import mx.edu.itson.proyectonova.models.dtos.DatosFisicosResponse;
import mx.edu.itson.proyectonova.models.entities.KeplerDataEntity;
import org.springframework.stereotype.Component;

/**
 * Componente responsable de mapear las entidades de la base de datos
 * a los objetos de transferencia de datos (DTOs) que consumira el frontend.
 */
@Component
public class KeplerMapper {

    /**
     * Constructor por defecto de la clase utilitaria para mapeo.
     */
    public KeplerMapper() {
    }

    /**
     * Transforma la telemetria completa de SQLite a los parametros visuales
     * exactos que el motor grafico de Three.js necesita, con conversion segura de Float a Double.
     *
     * @param entidad El registro de la base de datos con las 25 variables.
     * @return DTO limpio solo con las propiedades visuales.
     */
    public DatosFisicosResponse toDatosFisicosResponse(KeplerDataEntity entidad) {
        // Saneamiento basico de seguridad
        if (entidad == null) {
            return null;
        }

        // Construccion del DTO mapeando propiedades y realizando conversion segura (Float -> Double)
        return DatosFisicosResponse.builder()
                .kepid(entidad.getKepid())

                // Escala de la malla del exoplaneta
                .radioPlanetario(entidad.getKoiPrad() != null ? entidad.getKoiPrad().doubleValue() : null)

                // Tipo de bioma (ej. rocoso vs lava)
                .temperaturaEquilibrio(entidad.getKoiTeq() != null ? entidad.getKoiTeq().doubleValue() : null)

                // Escala de la malla de la estrella
                .radioEstelar(entidad.getKoiSrad() != null ? entidad.getKoiSrad().doubleValue() : null)

                // Colorimetria del shader (Kelvin a RGB)
                .temperaturaEstelar(entidad.getKoiSteff() != null ? entidad.getKoiSteff().doubleValue() : null)

                // Intensidad de la animacion de la corona
                .gravedadEstelar(entidad.getKoiSlogg() != null ? entidad.getKoiSlogg().doubleValue() : null)

                // Parametro orbital
                .duracionOrbital(entidad.getKoiDuration() != null ? entidad.getKoiDuration().doubleValue() : null)

                .build();
    }
}