package mx.edu.itson.proyectonova.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.itson.proyectonova.mappers.KeplerMapper;
import mx.edu.itson.proyectonova.models.dtos.DatosFisicosResponse;
import mx.edu.itson.proyectonova.models.entities.KeplerDataEntity;
import mx.edu.itson.proyectonova.repositories.KeplerRepository;
import mx.edu.itson.proyectonova.services.interfaces.TelemetriaService;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio encargado de proveer los datos visuales al motor gráfico.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetriaServiceImpl implements TelemetriaService {

    private final KeplerRepository keplerRepository; //[cite: 4]
    private final KeplerMapper keplerMapper;

    @Override
    public DatosFisicosResponse obtenerPropiedadesVisuales(String kepid) {
        log.info("Frontend solicitando propiedades visuales para el astro KEPID: {}", kepid);

        // 1. Buscamos el registro crudo en la base de datos
        KeplerDataEntity entidad = keplerRepository.findByKepid(kepid) //[cite: 4]
                .orElseThrow(() -> new RuntimeException("Imposible renderizar: Astro no encontrado en el catálogo de SQLite."));

        // 2. Mapeamos la entidad de 25 variables a un DTO ligero de solo 6 variables
        return keplerMapper.toDatosFisicosResponse(entidad);
    }
}