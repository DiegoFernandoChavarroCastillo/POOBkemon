package domain;

import java.io.Serializable;
/**
 * Enumeración que representa los diferentes tipos de efectos que pueden aplicarse
 * en el juego, como mejoras, debilitamientos, condiciones climáticas, etc.
 * Implementa {@link Serializable} para permitir la serialización de objetos que
 * contengan este tipo.
 */
public enum EffectType implements Serializable {
    BUFF, DEBUFF, STATUS, WEATHER, FORCE_SWITCH, RESET_STATS, RESTRICTION
}
