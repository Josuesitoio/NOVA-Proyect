package mx.edu.itson.proyectonova.services.impl;


import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import lombok.extern.slf4j.Slf4j;
import mx.edu.itson.proyectonova.exceptions.ResourceNotFoundException;
import mx.edu.itson.proyectonova.models.dtos.DatosFisicosResponse;
import mx.edu.itson.proyectonova.models.entities.KeplerDataEntity;
import mx.edu.itson.proyectonova.repositories.KeplerRepository;
import mx.edu.itson.proyectonova.services.interfaces.ExploracionService;
import mx.edu.itson.proyectonova.mappers.KeplerMapper;
import org.springframework.stereotype.Service;


/**
 * Implementación del servicio de exploración que utiliza el motor ONNX para realizar inferencias
 * y determinar la probabilidad de que un astro sea un exoplaneta confirmado.
 */
@Slf4j
@Service
public class ExploracionServiceImpl implements ExploracionService {

    private final KeplerRepository repository;
    private final KeplerMapper mapper;
    private final OrtEnvironment env;
    private final OrtSession session;

    /**
     * Constructor principal que inyecta las dependencias necesarias.
     * @param repository Repositorio de telemetría Kepler.
     * @param mapper Componente para mapear entidades a DTOs.
     * @param env Entorno de ejecución de ONNX.
     * @param session Sesión activa del modelo neuronal ONNX.
     */
    public ExploracionServiceImpl(KeplerRepository repository, KeplerMapper mapper, OrtEnvironment env, OrtSession session) {
        this.repository = repository;
        this.mapper = mapper;
        this.env = env;
        this.session = session;
    }

    @Override
    public DatosFisicosResponse analizarAstro(String kepid) {
        KeplerDataEntity entity = repository.findByKepid(kepid)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro telemetria para el astro: " + kepid));

        return procesarInferencia(entity);
    }

    @Override
    public DatosFisicosResponse analizarAstroManual(KeplerDataEntity entidadTemporario) {
        return procesarInferencia(entidadTemporario);
    }

    private DatosFisicosResponse procesarInferencia(KeplerDataEntity entidad) {
    //ESCRIBE TU CODIGO AQUI ----->
    }

    // Modificamos el parámetro para que reciba directamente el float[][]
    private float[] ejecutarInferencia(float[][] inputMatrix) {
    //ESCRIBE TU CODIGO AQUI ------>

}