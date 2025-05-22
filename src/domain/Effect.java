package domain;

import java.util.Map;

public class Effect {

    private EffectType effectType;
    private Map<String, Integer> statChanges;
    private String status;
    private int duration;
    private boolean stackable;
    private boolean forceSwitch;
    private Target target;


    public Effect(EffectType effectType,Target target, Map<String, Integer> statChanges, String status, int duration,
                  boolean stackable, boolean forceSwitch) {
        this.effectType = effectType;
        this.statChanges = statChanges;
        this.target = target;
        this.status = status;
        this.duration = duration;
        this.stackable = stackable;
        this.forceSwitch = forceSwitch;
    }

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

        // Guardar el efecto si es de duración (STATUS o RESTRICTION)
        if (duration > 0 && (effectType == EffectType.STATUS || effectType == EffectType.RESTRICTION)) {
            affected.addEffect(this);
        }

        // Caso especial: toxic tiene duración -1 pero debe guardarse
        if ("toxic".equalsIgnoreCase(status) && effectType == EffectType.STATUS) {
            affected.addEffect(this);
        }
    }


    public int getDuration() {
        return duration;
    }

    public String getStatus() {
        return status;
    }

    public Map<String, Integer> getStatChanges() {
        return statChanges;
    }

    public boolean isStackable() {
        return stackable;
    }

    public EffectType getEffectType() {
        return effectType;
    }
}
