package mx.edu.itson.proyectonova.utils;

import lombok.extern.slf4j.Slf4j;
import mx.edu.itson.proyectonova.models.entities.KeplerDataEntity;
import mx.edu.itson.proyectonova.exceptions.TensorBuildException;

@Slf4j
public class TensorBuilderUtil {

    // --- PASO 1: CONSTANTES DE IMPUTACIÓN (Medianas) ---
    private static final float MEDIANA_PERIOD = 9.752831f;
    private static final float MEDIANA_TIME0BK = 137.224595f;
    private static final float MEDIANA_IMPACT = 0.537f;
    private static final float MEDIANA_DURATION = 3.7926f;
    private static final float MEDIANA_DEPTH = 421.1f;
    private static final float MEDIANA_PRAD = 2.39f;
    private static final float MEDIANA_TEQ = 878.0f;
    private static final float MEDIANA_INSOL = 141.6f;
    private static final float MEDIANA_SNR = 23.0f;
    private static final float MEDIANA_STEFF = 5767.0f;
    private static final float MEDIANA_SLOGG = 4.438f;
    private static final float MEDIANA_SRAD = 1.0f;
    private static final float MEDIANA_RA = 292.261125f;
    private static final float MEDIANA_DEC = 43.677504f;
    private static final float MEDIANA_KEPMAG = 14.52f;

    // --- PASO 2: CONSTANTES DEL STANDARD SCALER ---
    private static final float[] MEDIAS = {
            78.835391f, 0.002020f, 166.120725f, 0.009191f, 0.730083f,
            1.938751f, 5.540922f, 0.318805f, 22938.301987f, 128.965586f,
            103.172794f, 16.298931f, 1081.105215f, 6797.579255f, 3333.595748f,
            250.243681f, 5710.547379f, 137.082473f, 4.314606f, 0.113931f,
            1.711411f, 0.344700f, 292.051659f, 43.785586f, 14.258131f
    };

    private static final float[] DESVIACIONES = {
            1490.958922f, 0.008205f, 68.680306f, 0.021898f, 3.255660f,
            9.428202f, 6.335429f, 0.657347f, 81725.853628f, 4484.201009f,
            3249.791039f, 346.153287f, 837.491012f, 117902.144204f, 39784.259987f,
            774.006418f, 779.673150f, 55.601748f, 0.427534f, 0.130968f,
            6.026852f, 0.887177f, 4.785962f, 3.589921f, 1.391420f
    };

    public static float[][] construirTensor(KeplerDataEntity entidad) {
        try {
            float[] caracteristicas = new float[25];

            // FASE 1: Extracción e Imputación de Nulos
            caracteristicas[0] = sanearFisica(entidad.getKoiPeriod(), MEDIANA_PERIOD);
            caracteristicas[1] = sanearError(entidad.getKoiPeriodErr1());
            caracteristicas[2] = sanearFisica(entidad.getKoiTime0bk(), MEDIANA_TIME0BK);
            caracteristicas[3] = sanearError(entidad.getKoiTime0bkErr1());
            caracteristicas[4] = sanearFisica(entidad.getKoiImpact(), MEDIANA_IMPACT);
            caracteristicas[5] = sanearError(entidad.getKoiImpactErr1());
            caracteristicas[6] = sanearFisica(entidad.getKoiDuration(), MEDIANA_DURATION);
            caracteristicas[7] = sanearError(entidad.getKoiDurationErr1());
            caracteristicas[8] = sanearFisica(entidad.getKoiDepth(), MEDIANA_DEPTH);
            caracteristicas[9] = sanearError(entidad.getKoiDepthErr1());
            caracteristicas[10] = sanearFisica(entidad.getKoiPrad(), MEDIANA_PRAD);
            caracteristicas[11] = sanearError(entidad.getKoiPradErr1());
            caracteristicas[12] = sanearFisica(entidad.getKoiTeq(), MEDIANA_TEQ);
            caracteristicas[13] = sanearFisica(entidad.getKoiInsol(), MEDIANA_INSOL);
            caracteristicas[14] = sanearError(entidad.getKoiInsolErr1());
            caracteristicas[15] = sanearFisica(entidad.getKoiModelSnr(), MEDIANA_SNR);
            caracteristicas[16] = sanearFisica(entidad.getKoiSteff(), MEDIANA_STEFF);
            caracteristicas[17] = sanearError(entidad.getKoiSteffErr1());
            caracteristicas[18] = sanearFisica(entidad.getKoiSlogg(), MEDIANA_SLOGG);
            caracteristicas[19] = sanearError(entidad.getKoiSloggErr1());
            caracteristicas[20] = sanearFisica(entidad.getKoiSrad(), MEDIANA_SRAD);
            caracteristicas[21] = sanearError(entidad.getKoiSradErr1());
            caracteristicas[22] = sanearFisica(entidad.getRa(), MEDIANA_RA);
            caracteristicas[23] = sanearFisica(entidad.getDec(), MEDIANA_DEC);
            caracteristicas[24] = sanearFisica(entidad.getKoiKepmag(), MEDIANA_KEPMAG);

            // FASE 2: Escalado de los datos (StandardScaler)
            for (int i = 0; i < 25; i++) {
                if (DESVIACIONES[i] != 0.0f) {
                    caracteristicas[i] = (caracteristicas[i] - MEDIAS[i]) / DESVIACIONES[i];
                } else {
                    caracteristicas[i] = caracteristicas[i] - MEDIAS[i];
                }
            }

            return new float[][]{caracteristicas};

        } catch(Exception e) {
            log.error("Fallo crítico al construir el tensor para KEPID: " + entidad.getKepid(), e);
            throw new TensorBuildException("Fallo al construir tensor para KEPID: " + entidad.getKepid(), e);
        }
    }

    private static float sanearFisica(Float valor, float mediana) {
        return (valor != null) ? valor : mediana;
    }

    private static float sanearError(Float valor) {
        return (valor != null) ? valor : 0.0f;
    }
}