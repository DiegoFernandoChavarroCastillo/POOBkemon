package domain;

import java.io.Serializable;
import java.util.Map;

/**
 * Representa un efecto aplicado por un movimiento en batalla.
 * Puede modificar estadísticas, aplicar estados, forzar cambios, entre otros.
 */
public class Effect implements Serializable {

    private EffectType effectType;
    private Map<String, Integer> statChanges;
    private String status;
    private int duration;
    private boolean stackable;
    private boolean forceSwitch;
    private Target target;

    /**
     * Construye un nuevo efecto con los parámetros especificados.
     *
     * @param effectType   tipo de efecto (BUFF, DEBUFF, STATUS, etc.)
     * @param target       objetivo del efecto (usuario o enemigo)
     * @param statChanges  mapa de cambios estadísticos (ej. "ataque", +1)
     * @param status       estado a aplicar (parálisis, toxic, etc.)
     * @param duration     duración del efecto en turnos
     * @param stackable    indica si el efecto puede acumularse
     * @param forceSwitch  indica si debe forzarse un cambio de Pokémon
     */
    public Effect(EffectType effectType, Target target, Map<String, Integer> statChanges, String status, int duration,
                  boolean stackable, boolean forceSwitch) {
        this.effectType = effectType;
        this.statChanges = statChanges;
        this.target = target;
        this.status = status;
        this.duration = duration;
        this.stackable = stackable;
        this.forceSwitch = forceSwitch;
    }

    /**
     * Aplica el efecto al Pokémon objetivo o al usuario, dependiendo del tipo de objetivo.
     *
     * @param user   el Pokémon que ejecuta el movimiento
     * @param target el Pokémon objetivo
     */
    public void apply(Pokemon user, Pokemon target) {
        Pokemon affected = (this.target == Target.USER) ? user : target;

        if ((effectType == EffectType.BUFF || effectType == EffectType.DEBUFF) && statChanges != null) {
            for (Map.Entry<String, Integer> entry : statChanges.entrySet()) {
                affected.modifyStat(entry.getKey(), entry.getValue());
            }
        }

        if (effectType == EffectType.STATUS && status != null) {
            affected.setStatus(status);
        }

        if (effectType == EffectType.FORCE_SWITCH && forceSwitch) {
            affected.setForcedToSwitch(true);
        }

        if (effectType == EffectType.RESET_STATS) {
            user.resetBoosts();
            target.resetBoosts();
        }

        if (effectType == EffectType.RESTRICTION && status != null) {
            affected.applyRestriction(status, duration);
        }

        // Guardar el efecto si tiene duración y es relevante
        if (duration > 0 && (effectType == EffectType.STATUS || effectType == EffectType.RESTRICTION)) {
            affected.addEffect(this);
        }

        // Caso especial: "toxic" debe guardarse incluso con duración -1
        if ("toxic".equalsIgnoreCase(status) && effectType == EffectType.STATUS) {
            affected.addEffect(this);
        }
    }

    /**
     * Obtiene la duración del efecto en turnos.
     *
     * @return duración del efecto
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Obtiene el estado asociado al efecto (si aplica).
     *
     * @return el estado como cadena (ej. "parálisis", "toxic")
     */
    public String getStatus() {
        return status;
    }

    /**
     * Obtiene el mapa de cambios estadísticos aplicados por el efecto.
     *
     * @return mapa con estadísticas y sus modificaciones
     */
    public Map<String, Integer> getStatChanges() {
        return statChanges;
    }

    /**
     * Indica si el efecto es acumulable.
     *
     * @return true si puede acumularse, false si no
     */
    public boolean isStackable() {
        return stackable;
    }

    /**
     * Retorna el tipo del efecto.
     *
     * @return tipo de efecto (BUFF, STATUS, etc.)
     */
    public EffectType getEffectType() {
        return effectType;
    }
}
