package mx.edu.itson.proyectonova.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.itson.proyectonova.services.interfaces.KeplerDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Controlador REST dedicado a la gestion y administracion de datos base.
 * Expone endpoints para la ingesta masiva de archivos generados por la NASA.
 */
@Slf4j
@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "*") // Permite peticiones desde el frontend Vanila sin bloqueos CORS
public class DataController {

    private final KeplerDataService keplerDataService;

    /**
     * Constructor principal para inyección de dependencias.
     * @param keplerDataService Servicio encargado de procesar la telemetría.
     */
    public DataController(KeplerDataService keplerDataService) {
        this.keplerDataService = keplerDataService;
    }

    /**
     * Endpoint para recibir y procesar el archivo CSV oficial.
     *
     * @param file Archivo binario adjunto en la peticion HTTP multipart/form-data.
     * @return Objeto JSON con el mensaje de exito o el detalle del error.
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            log.info("Peticion HTTP recibida: Carga de archivo de telemetria [{}]", file.getOriginalFilename());

            // Delegar el procesamiento pesado al servicio de capa de negocio
            String resultado = keplerDataService.procesarArchivoCsv(file);

            // Retornar 200 OK con un JSON estructurado
            return ResponseEntity.ok(Map.of("mensaje", resultado));

        } catch (IllegalArgumentException e) {
            // El archivo venia vacio o sin formato (400 Bad Request)
            log.warn("Peticion de carga rechazada: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            // Falla catastrofica en la base de datos o en el parseo (500 Internal Server Error)
            log.error("Falla en el endpoint de ingesta masiva: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al procesar el archivo CSV. Revise los logs de la consola."));
        }
    }
}