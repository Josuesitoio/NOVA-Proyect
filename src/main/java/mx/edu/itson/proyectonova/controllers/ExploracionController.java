package mx.edu.itson.proyectonova.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.itson.proyectonova.models.dtos.DatosFisicosResponse;
import mx.edu.itson.proyectonova.services.interfaces.ExploracionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mx.edu.itson.proyectonova.models.dtos.DatosKeplerRequest;
import mx.edu.itson.proyectonova.models.entities.KeplerDataEntity;

/**
 * Controlador REST principal para la interfaz de telemetria espacial.
 * Conecta el frontend 3D con el motor de inferencia ONNX Runtime.
 */
@Slf4j
@RestController
@RequestMapping("/api/exploracion")
@CrossOrigin(origins = "*") // Crucial para evitar bloqueos CORS si ejecutas el HTML directo en el navegador
public class ExploracionController {

    private final ExploracionService exploracionService;

    /**
     * Constructor principal para inyección de dependencias.
     * @param exploracionService Servicio que maneja la lógica de exploración.
     */
    public ExploracionController(ExploracionService exploracionService) {
        this.exploracionService = exploracionService;
    }

    /**
     * Recupera la telemetria fisica y ejecuta la prediccion de IA para un astro.
     *
     * @param kepid Identificador oficial del catalogo Kepler (Ej. K00001.01).
     * @return DTO empaquetado con estatus HTTP 200 OK.
     */
    @GetMapping("/{kepid}")
    public ResponseEntity<DatosFisicosResponse> obtenerTelemetria(@PathVariable String kepid) {
        log.info("Iniciando secuencia de exploracion para el astro: {}", kepid);

        // La logica pesada (SQLite + ONNX + Softmax) ocurre dentro de la capa de servicio
        DatosFisicosResponse respuesta = exploracionService.analizarAstro(kepid);

        log.info("Telemetria y prediccion listas. Transmitiendo al cliente WebGL.");
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Permite ejecutar la inferencia de manera manual inyectando variables directamente.
     * @param request DTO con las características astrofísicas.
     * @return DTO con la predicción del modelo y propiedades.
     */
    @PostMapping("/manual")
    public ResponseEntity<DatosFisicosResponse> probarModeloManual(@RequestBody DatosKeplerRequest request) {
        log.info("Iniciando prueba de inferencia manual en ONNX");

        // Transformamos el DTO de 25 variables estricto a una Entidad volátil
        KeplerDataEntity entidadTemporal = KeplerDataEntity.builder()
                .koiPeriod(request.getKoiPeriod()).koiPeriodErr1(request.getKoiPeriodErr1())
                .koiTime0bk(request.getKoiTime0bk()).koiTime0bkErr1(request.getKoiTime0bkErr1())
                .koiImpact(request.getKoiImpact()).koiImpactErr1(request.getKoiImpactErr1())
                .koiDuration(request.getKoiDuration()).koiDurationErr1(request.getKoiDurationErr1())
                .koiDepth(request.getKoiDepth()).koiDepthErr1(request.getKoiDepthErr1())
                .koiPrad(request.getKoiPrad()).koiPradErr1(request.getKoiPradErr1())
                .koiTeq(request.getKoiTeq())
                .koiInsol(request.getKoiInsol()).koiInsolErr1(request.getKoiInsolErr1())
                .koiModelSnr(request.getKoiModelSnr())
                .koiSteff(request.getKoiSteff()).koiSteffErr1(request.getKoiSteffErr1())
                .koiSlogg(request.getKoiSlogg()).koiSloggErr1(request.getKoiSloggErr1())
                .koiSrad(request.getKoiSrad()).koiSradErr1(request.getKoiSradErr1())
                .ra(request.getRa()).dec(request.getDec()).koiKepmag(request.getKoiKepmag())
                .build();

        DatosFisicosResponse respuesta = exploracionService.analizarAstroManual(entidadTemporal);
        return ResponseEntity.ok(respuesta);
    }
}