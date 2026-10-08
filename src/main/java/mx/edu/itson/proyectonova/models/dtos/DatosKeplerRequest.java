package mx.edu.itson.proyectonova.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada. Recibe telemetría astrofísica manual o vía JSON desde el cliente
 * para inyectarla directamente al modelo ONNX sin pasar por la base de datos.
 */
@Data
@Builder
@AllArgsConstructor
public class DatosKeplerRequest {

    /**
     * Constructor por defecto para la des-serialización del JSON de entrada.
     */
    public DatosKeplerRequest() {
    }

    // 1-2: Período Orbital
    @JsonProperty("koi_period")
    private Float koiPeriod;
    @JsonProperty("koi_period_err1")
    private Float koiPeriodErr1;

    // 3-4: Tiempo de Tránsito
    @JsonProperty("koi_time0bk")
    private Float koiTime0bk;
    @JsonProperty("koi_time0bk_err1")
    private Float koiTime0bkErr1;

    // 5-6: Parámetro de Impacto
    @JsonProperty("koi_impact")
    private Float koiImpact;
    @JsonProperty("koi_impact_err1")
    private Float koiImpactErr1;

    // 7-8: Duración del Tránsito
    @JsonProperty("koi_duration")
    private Float koiDuration;
    @JsonProperty("koi_duration_err1")
    private Float koiDurationErr1;

    // 9-10: Profundidad del Tránsito
    @JsonProperty("koi_depth")
    private Float koiDepth;
    @JsonProperty("koi_depth_err1")
    private Float koiDepthErr1;

    // 11-12: Radio Planetario
    @JsonProperty("koi_prad")
    private Float koiPrad;
    @JsonProperty("koi_prad_err1")
    private Float koiPradErr1;

    // 13: Temperatura de Equilibrio Planetario
    @JsonProperty("koi_teq")
    private Float koiTeq;

    // 14-15: Insolación Planetaria
    @JsonProperty("koi_insol")
    private Float koiInsol;
    @JsonProperty("koi_insol_err1")
    private Float koiInsolErr1;

    // 16: Relación Señal-Ruido del Tránsito
    @JsonProperty("koi_model_snr")
    private Float koiModelSnr;

    // 17-18: Temperatura Efectiva Estelar
    @JsonProperty("koi_steff")
    private Float koiSteff;
    @JsonProperty("koi_steff_err1")
    private Float koiSteffErr1;

    // 19-20: Gravedad Superficial Estelar
    @JsonProperty("koi_slogg")
    private Float koiSlogg;
    @JsonProperty("koi_slogg_err1")
    private Float koiSloggErr1;

    // 21-22: Radio Estelar
    @JsonProperty("koi_srad")
    private Float koiSrad;
    @JsonProperty("koi_srad_err1")
    private Float koiSradErr1;

    // 23-25: Coordenadas Celestiales y Magnitud (Estas no cambian)
    @JsonProperty("ra")
    private Float ra;
    @JsonProperty("dec")
    private Float dec;
    @JsonProperty("koi_kepmag")
    private Float koiKepmag;
}