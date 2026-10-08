package mx.edu.itson.proyectonova.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.itson.proyectonova.models.entities.KeplerDataEntity;
import mx.edu.itson.proyectonova.models.enums.ClasificacionEnum; // Importación obligatoria del Enum
import mx.edu.itson.proyectonova.repositories.KeplerRepository;
import mx.edu.itson.proyectonova.services.interfaces.KeplerDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación del servicio de procesamiento de datos de Kepler.
 * Maneja la lectura de archivos CSV y la persistencia de datos en la base de datos.
 */
@Slf4j
@Service
public class KeplerDataServiceImpl implements KeplerDataService {

    private final KeplerRepository repository;
    private static final int BATCH_SIZE = 1000;

    /**
     * Constructor principal para inyección de dependencias.
     * @param repository Repositorio de telemetría Kepler.
     */
    public KeplerDataServiceImpl(KeplerRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public String procesarArchivoCsv(MultipartFile archivo) {
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo CSV esta vacio o no fue enviado.");
        }
        log.info("Iniciando ingesta masiva del archivo: {}", archivo.getOriginalFilename());
        List<KeplerDataEntity> lote = new ArrayList<>();
        int registrosProcesados = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {
            String linea;
            Map<String, Integer> mapaColumnas = new HashMap<>();
            boolean cabecerasLeidas = false;

            while ((linea = br.readLine()) != null) {
                // 1. Ignorar lineas de comentarios
                if (linea.trim().startsWith("#")) {
                    continue;
                }
                String[] valores = linea.split(",", -1);

                // 2. Mapeo dinamico de cabeceras
                if (!cabecerasLeidas) {
                    for (int i = 0; i < valores.length; i++) {
                        mapaColumnas.put(valores[i].trim().toLowerCase(), i);
                    }
                    cabecerasLeidas = true;
                    continue;
                }

                // 3. Extraccion segura de datos
                KeplerDataEntity entidad = new KeplerDataEntity();

                String kepid = extraerString(valores, mapaColumnas, "kepid");
                if (kepid == null || kepid.isEmpty()) {
                    continue;
                }
                entidad.setKepid(kepid);

                // Extracción y mapeo del Enum de Clasificación
                String disposicion = extraerString(valores, mapaColumnas, "koi_disposition");
                if (disposicion != null && !disposicion.isEmpty()) {
                    // Convierte el string del CSV al formato del Enum (Ej. "FALSE POSITIVE" a "FALSE_POSITIVE")
                    entidad.setKoiDisposition(ClasificacionEnum.valueOf(disposicion.toUpperCase().replace(" ", "_")));
                } else {
                    entidad.setKoiDisposition(ClasificacionEnum.CANDIDATE); // Valor por defecto seguro en caso de venir vacío
                }

                // Asignacion estricta como Float
                entidad.setKoiPeriod(extraerFloat(valores, mapaColumnas, "koi_period"));
                entidad.setKoiPeriodErr1(extraerFloat(valores, mapaColumnas, "koi_period_err1"));
                entidad.setKoiTime0bk(extraerFloat(valores, mapaColumnas, "koi_time0bk"));
                entidad.setKoiTime0bkErr1(extraerFloat(valores, mapaColumnas, "koi_time0bk_err1"));
                entidad.setKoiImpact(extraerFloat(valores, mapaColumnas, "koi_impact"));
                entidad.setKoiImpactErr1(extraerFloat(valores, mapaColumnas, "koi_impact_err1"));
                entidad.setKoiDuration(extraerFloat(valores, mapaColumnas, "koi_duration"));
                entidad.setKoiDurationErr1(extraerFloat(valores, mapaColumnas, "koi_duration_err1"));
                entidad.setKoiDepth(extraerFloat(valores, mapaColumnas, "koi_depth"));
                entidad.setKoiDepthErr1(extraerFloat(valores, mapaColumnas, "koi_depth_err1"));
                entidad.setKoiPrad(extraerFloat(valores, mapaColumnas, "koi_prad"));
                entidad.setKoiPradErr1(extraerFloat(valores, mapaColumnas, "koi_prad_err1"));
                entidad.setKoiTeq(extraerFloat(valores, mapaColumnas, "koi_teq"));
                entidad.setKoiInsol(extraerFloat(valores, mapaColumnas, "koi_insol"));
                entidad.setKoiInsolErr1(extraerFloat(valores, mapaColumnas, "koi_insol_err1"));
                entidad.setKoiModelSnr(extraerFloat(valores, mapaColumnas, "koi_model_snr"));
                entidad.setKoiSteff(extraerFloat(valores, mapaColumnas, "koi_steff"));
                entidad.setKoiSteffErr1(extraerFloat(valores, mapaColumnas, "koi_steff_err1"));
                entidad.setKoiSlogg(extraerFloat(valores, mapaColumnas, "koi_slogg"));
                entidad.setKoiSloggErr1(extraerFloat(valores, mapaColumnas, "koi_slogg_err1"));
                entidad.setKoiSrad(extraerFloat(valores, mapaColumnas, "koi_srad"));
                entidad.setKoiSradErr1(extraerFloat(valores, mapaColumnas, "koi_srad_err1"));
                entidad.setRa(extraerFloat(valores, mapaColumnas, "ra"));
                entidad.setDec(extraerFloat(valores, mapaColumnas, "dec"));
                entidad.setKoiKepmag(extraerFloat(valores, mapaColumnas, "koi_kepmag"));

                lote.add(entidad);
                registrosProcesados++;

                // 4. Volcado a SQLite por lotes
                if (lote.size() >= BATCH_SIZE) {
                    repository.saveAll(lote);
                    lote.clear();
                    log.info("Lote insertado en SQLite. Total procesados: {}", registrosProcesados);
                }
            }

            if (!lote.isEmpty()) {
                repository.saveAll(lote);
                log.info("Lote final insertado. Total procesados: {}", registrosProcesados);
            }

            return "Exito. Se procesaron e insertaron " + registrosProcesados + " registros astro-fisicos en SQLite.";

        } catch (Exception e) {
            log.error("Fallo critico durante la lectura del archivo CSV: ", e);
            throw new RuntimeException("Error al procesar el archivo CSV: " + e.getMessage());
        }
    }

    private String extraerString(String[] valores, Map<String, Integer> mapaColumnas, String columna) {
        Integer index = mapaColumnas.get(columna);
        if (index != null && index < valores.length) {
            String valor = valores[index].trim();
            return valor.isEmpty() ? null : valor;
        }
        return null;
    }

    /**
     * Helper de extraccion optimizado para Float (32-bit).
     */
    private Float extraerFloat(String[] valores, Map<String, Integer> mapaColumnas, String columna) {
        String valorStr = extraerString(valores, mapaColumnas, columna);
        if (valorStr != null) {
            try {
                return Float.parseFloat(valorStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}