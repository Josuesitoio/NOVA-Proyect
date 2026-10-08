package mx.edu.itson.proyectonova.repositories;

import mx.edu.itson.proyectonova.models.entities.KeplerDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para interactuar con la base de datos SQLite.
 *
 * Propósito arquitectónico:
 * Al extender de JpaRepository, Spring Boot escribe mágicamente por ti
 * todo el código SQL en tiempo de ejecución (INSERT, SELECT, UPDATE, DELETE).
 * Actúa como un puente directo entre tu base de datos y tus objetos de Java.
 */
@Repository
public interface KeplerRepository extends JpaRepository<KeplerDataEntity, Long> {

    /**
     * Busca un objeto en el catálogo por su ID exacto de la NASA.
     * Spring Data lee el nombre del método y construye automáticamente la consulta:
     * SELECT * FROM kepler_data WHERE kepid = ?
     *
     * @param kepid El ID oficial del catálogo Kepler.
     * @return Optional con la entidad si existe, para evitar los temidos NullPointerExceptions.
     */
    Optional<KeplerDataEntity> findByKepid(String kepid);

    /**
     * Recupera todos los registros que el modelo de IA aún no ha procesado.
     * Spring Data lo traduce a:
     * SELECT * FROM kepler_data WHERE is_processed = false
     *
     * @return Lista de planetas esperando ser analizados por el motor ONNX.
     */
    List<KeplerDataEntity> findByIsProcessedFalse();
}
