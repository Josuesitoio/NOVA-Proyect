package mx.edu.itson.proyectonova.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.edu.itson.proyectonova.models.enums.ClasificacionEnum;

/**
 * Mapeo Objeto-Relacional (ORM) para la tabla de telemetría astrofísica.
 *
 * Propósito arquitectónico:
 * Representa un registro exacto en la base de datos SQLite. Al usar Float (objeto) en lugar
 * de float (primitivo), permitimos que la base de datos almacene valores nulos, lo cual es
 * vital porque los datasets crudos de la NASA suelen venir con datos faltantes.
 */
@Entity
@Table(name = "kepler_data")
@Data
@AllArgsConstructor
@Builder
public class KeplerDataEntity {

    /**
     * Constructor por defecto requerido por JPA.
     */
    public KeplerDataEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kepid", unique = true, nullable = false)
    private String kepid;


    @Enumerated(EnumType.STRING)
    @Column(name = "koi_disposition", nullable = false)
    private ClasificacionEnum koiDisposition;

    // 1-2: Período Orbital
    @Column(name = "koi_period")
    private Float koiPeriod;
    @Column(name = "koi_period_err1")
    private Float koiPeriodErr1;

    // 3-4: Tiempo de Tránsito
    @Column(name = "koi_time0bk")
    private Float koiTime0bk;
    @Column(name = "koi_time0bk_err1")
    private Float koiTime0bkErr1;

    // 5-6: Parámetro de Impacto
    @Column(name = "koi_impact")
    private Float koiImpact;
    @Column(name = "koi_impact_err1")
    private Float koiImpactErr1;

    // 7-8: Duración del Tránsito
    @Column(name = "koi_duration")
    private Float koiDuration;
    @Column(name = "koi_duration_err1")
    private Float koiDurationErr1;

    // 9-10: Profundidad del Tránsito
    @Column(name = "koi_depth")
    private Float koiDepth;
    @Column(name = "koi_depth_err1")
    private Float koiDepthErr1;

    // 11-12: Radio Planetario
    @Column(name = "koi_prad")
    private Float koiPrad;
    @Column(name = "koi_prad_err1")
    private Float koiPradErr1;

    // 13: Temperatura de Equilibrio Planetario
    @Column(name = "koi_teq")
    private Float koiTeq;

    // 14-15: Insolación Planetaria
    @Column(name = "koi_insol")
    private Float koiInsol;
    @Column(name = "koi_insol_err1")
    private Float koiInsolErr1;

    // 16: Relación Señal-Ruido del Tránsito
    @Column(name = "koi_model_snr")
    private Float koiModelSnr;

    // 17-18: Temperatura Efectiva Estelar
    @Column(name = "koi_steff")
    private Float koiSteff;
    @Column(name = "koi_steff_err1")
    private Float koiSteffErr1;

    // 19-20: Gravedad Superficial Estelar
    @Column(name = "koi_slogg")
    private Float koiSlogg;
    @Column(name = "koi_slogg_err1")
    private Float koiSloggErr1;

    // 21-22: Radio Estelar
    @Column(name = "koi_srad")
    private Float koiSrad;
    @Column(name = "koi_srad_err1")
    private Float koiSradErr1;

    // 23-25: Coordenadas Celestiales y Magnitud
    @Column(name = "ra")
    private Float ra; // Ascensión recta
    @Column(name = "dec")
    private Float dec; // Declinación
    @Column(name = "koi_kepmag")
    private Float koiKepmag; // Magnitud en la banda Kepler

    // Metadatos de control interno
    @Column(name = "is_processed")
    @Builder.Default
    private Boolean isProcessed = false;
}