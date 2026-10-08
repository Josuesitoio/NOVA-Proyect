cat << 'INNER_EOF' > src/main/java/mx/edu/itson/proyectonova/models/enums/ClasificacionEnum.java
package mx.edu.itson.proyectonova.models.enums;

/**
 * Representa el estado de clasificación oficial de la NASA para un objeto de interés Kepler (KOI).
 */
public enum ClasificacionEnum {
    POSITIVE,
    CANDIDATE,
    FALSE_POSITIVE
}
INNER_EOF
