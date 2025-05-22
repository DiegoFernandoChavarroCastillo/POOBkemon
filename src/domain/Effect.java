package domain;

import java.util.Map;

public class Effect {

    public enum Type {
        BUFF, DEBUFF, STATUS, FORCE_SWITCH, RESET_STATS, RESTRICTION
        // WEATHER fue eliminado aquí
    }

    private EffectType effectType;
    private Map<String, Integer> statChanges;
    private String status;
    private int duration;
    private boolean stackable;
    private boolean forceSwitch;

    public Effect(EffectType effectType, Map<String, Integer> statChanges, String status, int duration,
                  boolean stackable, boolean forceSwitch) {
        this.effectType = effectType;
        this.statChanges = statChanges;
        this.status = status;
        this.duration = duration;
        this.stackable = stackable;
        this.forceSwitch = forceSwitch;
    }

        public void apply(Pokemon user, Pokemon target) {
            if (effectType == EffectType.BUFF || effectType == EffectType.DEBUFF) {
                if (statChanges != null) {
                    for (String stat : statChanges.keySet()) {
                        target.modifyStat(stat, statChanges.get(stat));
                    }
                }
            }

            if (effectType == EffectType.STATUS && status != null) {
                target.setStatus(status);
            }

        if (effectType == EffectType.FORCE_SWITCH && forceSwitch) {
            target.setForcedToSwitch(true);
        }

        if (effectType == EffectType.RESET_STATS) {
            user.resetBoosts();
            target.resetBoosts();
        }

        if (effectType == EffectType.RESTRICTION && status != null) {
            target.applyRestriction(status, duration);
        }

        if (duration > 0 && (effectType == EffectType.STATUS || effectType == EffectType.RESTRICTION)) {
            target.addEffect(this);
        }

        if ((duration > 0 || "toxic".equals(status)) && effectType == EffectType.STATUS) {
            target.addEffect(this);
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
