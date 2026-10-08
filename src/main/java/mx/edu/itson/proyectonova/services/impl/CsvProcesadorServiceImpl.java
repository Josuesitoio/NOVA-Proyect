package mx.edu.itson.proyectonova.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.itson.proyectonova.models.entities.KeplerDataEntity;
import mx.edu.itson.proyectonova.models.enums.ClasificacionEnum;
import mx.edu.itson.proyectonova.repositories.KeplerRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio encargado de procesar de manera masiva los archivos CSV de la NASA,
 * extrayendo los datos y persistiendo lotes de telemetría en la base de datos local.
 */
@Slf4j
@Service
public class CsvProcesadorServiceImpl {

    private final KeplerRepository keplerRepository;

    /**
     * Constructor principal para la inyección de dependencias.
     * @param keplerRepository Repositorio JPA para entidades de Kepler.
     */
    public CsvProcesadorServiceImpl(KeplerRepository keplerRepository) {
        this.keplerRepository = keplerRepository;
    }

    /**
     * Procesa el CSV de la NASA de forma asíncrona para no bloquear el servidor.
     * Lee línea por línea, parsea los flotantes y guarda en lotes (batch) en SQLite.
     * @param csvInputStream El flujo de entrada de datos binarios del archivo CSV.
     */
    @Async
    @Transactional
    public void procesarCatalogoNasa(InputStream csvInputStream) {
        log.info("Iniciando procesamiento masivo del catálogo CSV...");

        List<KeplerDataEntity> lotePlanetas = new ArrayList<>(); //[cite: 2]
        int tamanoLote = 500; // Guardamos de 500 en 500 para optimizar SQLite
        int lineasProcesadas = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvInputStream))) {
            String linea;
            boolean esPrimeraLinea = true;

            while ((linea = br.readLine()) != null) {
                // Saltamos los comentarios del dataset original (empiezan con #) y la cabecera
                if (linea.startsWith("#") || linea.trim().isEmpty()) continue;
                if (esPrimeraLinea) {
                    esPrimeraLinea = false;
                    continue;
                }

                String[] columnas = linea.split(","); // Asumiendo delimitador estándar

                try {
                    KeplerDataEntity entidad = construirEntidadDesdeFila(columnas); //[cite: 2]
                    lotePlanetas.add(entidad);
                    lineasProcesadas++;

                    // Guardar el lote y liberar memoria
                    if (lotePlanetas.size() >= tamanoLote) {
                        keplerRepository.saveAll(lotePlanetas); //[cite: 4]
                        lotePlanetas.clear();
                        log.info("Lote guardado. Total procesados hasta ahora: {}", lineasProcesadas);
                    }
                } catch (Exception e) {
                    log.warn("Fila descartada por formato inválido o datos corruptos. Kepler ID: {}", columnas[0]);
                }
            }

            // Guardar el remanente si quedó algún registro suelto en la lista
            if (!lotePlanetas.isEmpty()) {
                keplerRepository.saveAll(lotePlanetas); //[cite: 4]
            }

            log.info("Procesamiento finalizado con éxito. Total de exoplanetas ingresados: {}", lineasProcesadas);

        } catch (Exception e) {
            log.error("Fallo crítico al leer el archivo CSV", e);
            throw new RuntimeException("Error en el procesamiento del CSV", e);
        }
    }

    /**
     * Mapea los strings del CSV directamente a la entidad de persistencia.
     */
    private KeplerDataEntity construirEntidadDesdeFila(String[] cols) {
        // Se asume que el índice de las columnas corresponde a tu dataset original de Kaggle/NASA
        return KeplerDataEntity.builder()
                .kepid(cols[0])
                .koiDisposition(ClasificacionEnum.valueOf(cols[1].toUpperCase().replace(" ", "_"))) //[cite: 3]
                .koiPeriod(parsearFloat(cols[2]))
                .koiPeriodErr1(parsearFloat(cols[3]))
                .koiTime0bk(parsearFloat(cols[4]))
                .koiTime0bkErr1(parsearFloat(cols[5]))
                .koiImpact(parsearFloat(cols[6]))
                .koiImpactErr1(parsearFloat(cols[7]))
                .koiDuration(parsearFloat(cols[8]))
                .koiDurationErr1(parsearFloat(cols[9]))
                .koiDepth(parsearFloat(cols[10]))
                .koiDepthErr1(parsearFloat(cols[11]))
                .koiPrad(parsearFloat(cols[12]))
                .koiPradErr1(parsearFloat(cols[13]))
                .koiTeq(parsearFloat(cols[14]))
                .koiInsol(parsearFloat(cols[15]))
                .koiInsolErr1(parsearFloat(cols[16]))
                .koiModelSnr(parsearFloat(cols[17]))
                .koiSteff(parsearFloat(cols[18]))
                .koiSteffErr1(parsearFloat(cols[19]))
                .koiSlogg(parsearFloat(cols[20]))
                .koiSloggErr1(parsearFloat(cols[21]))
                .koiSrad(parsearFloat(cols[22]))
                .koiSradErr1(parsearFloat(cols[23]))
                .ra(parsearFloat(cols[24]))
                .dec(parsearFloat(cols[25]))
                .koiKepmag(parsearFloat(cols[26]))
                .isProcessed(false)
                .build();
    }

    /**
     * Convierte strings a Float, tolerando celdas vacías del CSV.
     */
    private Float parsearFloat(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null; // El null está permitido por usar objetos Float en la entidad.[cite: 2]
        }
        return Float.parseFloat(valor.trim());
    }
}