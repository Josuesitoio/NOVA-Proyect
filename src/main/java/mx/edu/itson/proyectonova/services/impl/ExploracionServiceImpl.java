package mx.edu.itson.proyectonova.services.impl;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.itson.proyectonova.exceptions.ResourceNotFoundException;
import mx.edu.itson.proyectonova.models.dtos.DatosFisicosResponse;
import mx.edu.itson.proyectonova.models.entities.KeplerDataEntity;
import mx.edu.itson.proyectonova.repositories.KeplerRepository;
import mx.edu.itson.proyectonova.services.interfaces.ExploracionService;
import mx.edu.itson.proyectonova.mappers.KeplerMapper;
import mx.edu.itson.proyectonova.utils.TensorBuilderUtil;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExploracionServiceImpl implements ExploracionService {

    private final KeplerRepository repository;
    private final KeplerMapper mapper;

    private final OrtEnvironment env;
    private final OrtSession session;

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
        // 1. Delegamos la extracción, imputación y estandarización a la clase que arreglamos
        float[][] matrizCaracteristicas = TensorBuilderUtil.construirTensor(entidad);

        // 2. Ejecutamos la inferencia con la matriz ya escalada
        float[] probabilidades = ejecutarInferencia(matrizCaracteristicas);

        log.info("--- DIAGNÓSTICO ONNX PARA {} ---", entidad.getKepid());
        log.info("Índice [0] (CANDIDATE): {}%", probabilidades[0] * 100);
        log.info("Índice [1] (CONFIRMED): {}%", probabilidades[1] * 100);
        log.info("Índice [2] (FALSE POSITIVE): {}%", probabilidades[2] * 100);

        // 3. Corregimos el índice: CONFIRMED es el 1, FALSE POSITIVE es el 2
        int indiceConfirmado = 1;

        double confianzaExoplaneta = probabilidades[indiceConfirmado];
        boolean esConfirmado = confianzaExoplaneta > 0.85;

        DatosFisicosResponse response = mapper.toDatosFisicosResponse(entidad);
        if(response == null) {
            response = new DatosFisicosResponse();
        }

        response.setProbabilidadExoplaneta(confianzaExoplaneta);
        response.setConfirmadoPorIA(esConfirmado);

        return response;
    }

    // Modificamos el parámetro para que reciba directamente el float[][]
    private float[] ejecutarInferencia(float[][] inputMatrix) {
        try {
            try (OnnxTensor tensorEntrada = OnnxTensor.createTensor(this.env, inputMatrix)) {
                String nombreInput = this.session.getInputNames().iterator().next();
                try (OrtSession.Result resultado = this.session.run(Collections.singletonMap(nombreInput, tensorEntrada))) {
                    float[][] outputMatrix = (float[][]) resultado.get(0).getValue();
                    return outputMatrix[0];
                }
            }
        } catch (OrtException e) {
            log.error("Colapso durante el calculo del tensor ONNX: ", e);
            throw new RuntimeException("Error en la unidad de procesamiento de IA.");
        }
    }
}