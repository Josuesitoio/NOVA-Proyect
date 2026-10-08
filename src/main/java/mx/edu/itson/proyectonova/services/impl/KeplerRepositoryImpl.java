package mx.edu.itson.proyectonova.services.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación personalizada para operaciones complejas en la base de datos
 * que escapan a la convención estándar de Spring Data JPA.
 */
@Slf4j
@Repository
public class KeplerRepositoryImpl {

    /**
     * Constructor por defecto para la inyección de dependencias de Spring.
     */
    public KeplerRepositoryImpl() {
    }

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Vacía la tabla de telemetría de forma masiva utilizando una consulta nativa.
     * Ideal para limpiar la base de datos antes de una nueva ingesta del CSV de la NASA.
     */
    @Transactional
    public void truncarTabla() {
        log.warn("ATENCIÓN: Ejecutando borrado masivo de la tabla kepler_data en SQLite.");

        // Ejecución directa de SQL para maximizar el rendimiento en borrados masivos
        int registrosBorrados = entityManager.createNativeQuery("DELETE FROM kepler_data").executeUpdate();

        log.info("Operación de truncado exitosa. Registros eliminados: {}", registrosBorrados);
    }
}