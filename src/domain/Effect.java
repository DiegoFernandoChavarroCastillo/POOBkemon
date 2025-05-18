package domain;

import java.io.Serializable;
import java.util.Map;

/**
 * Representa un efecto aplicado por un movimiento durante la batalla. Los efectos pueden
 * incluir cambios de estadísticas, cambios de clima, aplicación de estados, restricciones,
 * creación de sustitutos, entre otros.
 */
public class Effect implements Serializable {
    private final String type;
    private final String target;
    private final Map<String, Integer> statChanges;
    private final String status;
    private final int duration;
    private static final long serialVersionUID = 1L;

    /**
     * Crea un nuevo efecto con sus propiedades específicas.
     *
     * @param type Tipo de efecto (por ejemplo: statChange, status, climate, etc.)
     * @param target Objetivo del efecto (self, opponent, field)
     * @param statChanges Cambios de estadísticas si aplica, puede ser null
     * @param status Estado o valor asociado (por ejemplo: "toxic", "sunny", etc.)
     * @param duration Duración del efecto en turnos, si aplica
     */
    public Effect(String type, String target, Map<String, Integer> statChanges, String status, int duration) {
        this.type = type.toLowerCase();
        this.target = target.toLowerCase();
        this.statChanges = statChanges;
        this.status = status;
        this.duration = duration;
    }

    /**
     * Aplica el efecto al objetivo correspondiente, en función del tipo y objetivo definidos.
     *
     * @param user Pokémon que utiliza el movimiento
     * @param opponent Pokémon oponente
     */
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

    /**
     * Devuelve el tipo del efecto.
     *
     * @return Tipo del efecto
     */
    public String getType() {
        return type;
    }

    /**
     * Devuelve el objetivo del efecto.
     *
     * @return Objetivo del efecto (self, opponent, etc.)
     */
    public String getTarget() {
        return target;
    }

    /**
     * Devuelve el mapa de cambios de estadísticas, si aplica.
     *
     * @return Mapa de cambios de estadísticas
     */
    public Map<String, Integer> getStatChanges() {
        return statChanges;
    }

    /**
     * Devuelve el estado o condición que representa el efecto.
     *
     * @return Estado o condición (por ejemplo: "paralysis", "rain", etc.)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Devuelve la duración del efecto en turnos.
     *
     * @return Duración en turnos
     */
    public int getDuration() {
        return duration;
    }
}
