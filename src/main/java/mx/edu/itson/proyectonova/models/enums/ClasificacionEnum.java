package mx.edu.itson.proyectonova.models.enums;

/**
 * Representa el estado de clasificación oficial de la NASA para un objeto de interés Kepler (KOI).
 */
public enum ClasificacionEnum {
    /** Objeto confirmado como exoplaneta. */
    CONFIRMED,
    /** Objeto candidato a exoplaneta. */
    CANDIDATE,
    /** Objeto clasificado como falso positivo. */
    FALSE_POSITIVE
}
