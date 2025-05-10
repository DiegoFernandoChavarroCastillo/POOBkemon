package domain;

import java.util.Map;

/**
 * Representa un efecto aplicado por un movimiento.
 */
public class Effect {
    private final String type; // statChange, status, climate, restriction, etc.
    private final String target; // self, opponent, field
    private final Map<String, Integer> statChanges; // Ej: {"attack": 1, "defense": -2}
    private final String status; // Ej: toxic, taunt, encore, etc.
    private final int duration; // en turnos, si aplica

    public Effect(String type, String target, Map<String, Integer> statChanges, String status, int duration) {
        this.type = type.toLowerCase();
        this.target = target.toLowerCase();
        this.statChanges = statChanges;
        this.status = status;
        this.duration = duration;
    }

    public void apply(Pokemon user, Pokemon opponent) {
        Pokemon targetPokemon = switch (target) {
            case "self" -> user;
            case "opponent" -> opponent;
            default -> null;
        };

        if (targetPokemon == null) return;

        switch (type) {
            case "statchange" -> {
                if (statChanges != null) {
                    for (Map.Entry<String, Integer> entry : statChanges.entrySet()) {
                        targetPokemon.modifyStat(entry.getKey(), entry.getValue());
                    }
                }
            }
            case "status" -> {
                if (status != null) {
                    targetPokemon.setStatus(status);
                }
            }
            case "climate" -> {
                if (status != null) {
                    Battle.setClimate(status, duration);
                }
            }
            case "clearboosts" -> targetPokemon.resetBoosts();
            case "substitute" -> targetPokemon.createSubstitute();
            case "restriction" -> targetPokemon.applyRestriction(status, duration);
            default -> {
            }
        }
    }


    public String getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public Map<String, Integer> getStatChanges() {
        return statChanges;
    }

    public String getStatus() {
        return status;
    }

    public int getDuration() {
        return duration;
    }
}
