package domain;

import java.util.Map;

public class Effect {

    public enum Type {
        BUFF, DEBUFF, STATUS, FORCE_SWITCH, RESET_STATS, RESTRICTION
        // WEATHER fue eliminado aquí
    }

    private Type type;
    private Map<String, Integer> statChanges;
    private String status;
    private int duration;
    private boolean stackable;
    private boolean forceSwitch;

    public Effect(Type type, Map<String, Integer> statChanges, String status, int duration,
                  boolean stackable, boolean forceSwitch) {
        this.type = type;
        this.statChanges = statChanges;
        this.status = status;
        this.duration = duration;
        this.stackable = stackable;
        this.forceSwitch = forceSwitch;
    }

    public void apply(Pokemon user, Pokemon target) {
        if (type == Type.BUFF || type == Type.DEBUFF) {
            if (statChanges != null) {
                for (String stat : statChanges.keySet()) {
                    target.modifyStat(stat, statChanges.get(stat));
                }
            }
        }

        if (type == Type.STATUS && status != null) {
            target.setStatus(status);
        }

        if (type == Type.FORCE_SWITCH && forceSwitch) {
            target.setForcedToSwitch(true);
        }

        if (type == Type.RESET_STATS) {
            user.resetBoosts();
            target.resetBoosts();
        }

        if (type == Type.RESTRICTION && status != null) {
            target.applyRestriction(status, duration);
        }

        if (duration > 0 && (type == Type.STATUS || type == Type.RESTRICTION)) {
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

    public Type getType() {
        return type;
    }
}
