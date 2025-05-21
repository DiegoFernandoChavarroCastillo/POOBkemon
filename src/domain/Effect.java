package domain;

import java.util.Map;

public class Effect {
    public enum Type {
        BUFF, DEBUFF, STATUS, WEATHER, FORCE_SWITCH, RESET_STATS, RESTRICTION
    }

    public enum Target {
        USER, OPPONENT
    }

    private Type type;
    private Target target;
    private Map<String, Integer> statChanges; // ej: {"attack": +1, "defense": -1}
    private String status; // "burned", "poisoned", "toxic", "paralyzed", etc.
    private int duration; // -1 si es permanente hasta curarse
    private boolean stackable;
    private boolean forceSwitch;

    public Effect(Type type, Target target, Map<String, Integer> statChanges, String status, int duration,
                  boolean stackable, boolean forceSwitch) {
        this.type = type;
        this.target = target;
        this.statChanges = statChanges;
        this.status = status;
        this.duration = duration;
        this.stackable = stackable;
        this.forceSwitch = forceSwitch;
    }

    public void apply(Pokemon user, Pokemon opponent) {
        Pokemon affected = target == Target.USER ? user : opponent;

        if (type == Type.BUFF || type == Type.DEBUFF) {
            if (statChanges != null) {
                for (String stat : statChanges.keySet()) {
                    affected.modifyStat(stat, statChanges.get(stat));
                }
            }
        }

        if (type == Type.STATUS && status != null) {
            affected.setStatus(status);
        }

        if (type == Type.FORCE_SWITCH && forceSwitch) {
            affected.setForcedToSwitch(true); // Debe implementarse este atributo
        }

        // Climas o efectos persistentes deberían manejarse en Battle o GameController
        if (type == Type.WEATHER) {
            user.getBattle().setWeather(status, duration); // método a definir en Battle si aún no existe
        }

        if (type == Type.RESET_STATS) {
            user.resetBoosts();
            opponent.resetBoosts();
        }

        if (type == Type.RESTRICTION && status != null) {
            affected.applyRestriction(status, duration);
        }

        // Almacenamiento si el efecto es de duración
        if (duration > 0 && (type == Type.STATUS || type == Type.RESTRICTION)) {
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

    public Type getType() {
        return type;
    }

    public Target getTarget() {
        return target;
    }
}
