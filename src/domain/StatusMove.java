package domain;

import java.io.Serializable;
import java.util.Random;

/**
 * Representa un movimiento de estado que aplica un efecto sin causar daño directo.
 * Este tipo de movimiento puede alterar estadísticas, aplicar restricciones o estados alterados.
 */
public class StatusMove extends Move implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String type;
    private final int precision;
    private final int maxPP;
    private final int priority;
    private int currentPP;
    private final Effect effect;

    /**
     * Crea un nuevo movimiento de estado con los parámetros especificados.
     *
     * @param name      nombre del movimiento
     * @param type      tipo del movimiento (por ejemplo, "NORMAL", "FUEGO", etc.)
     * @param precision precisión del movimiento (valor de 0 a 100)
     * @param maxPP     puntos de poder máximos (PP)
     * @param priority  prioridad del movimiento dentro del turno
     * @param effect    efecto que se aplicará si el movimiento acierta
     */
    public StatusMove(String name, String type, int precision, int maxPP, int priority, Effect effect) {
        this.name = name;
        this.type = type;
        this.precision = precision;
        this.maxPP = maxPP;
        this.priority = priority;
        this.effect = effect;
        this.currentPP = maxPP;
    }

    /**
     * Retorna el nombre del movimiento.
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * Retorna el tipo del movimiento.
     */
    @Override
    public String type() {
        return type;
    }

    /**
     * Retorna el poder del movimiento.
     * Para movimientos de estado, el valor siempre es 0.
     */
    @Override
    public int power() {
        return 0;
    }

    /**
     * Retorna la precisión del movimiento.
     */
    @Override
    public int precision() {
        return precision;
    }

    /**
     * Retorna los PP actuales del movimiento.
     */
    @Override
    public int pp() {
        return currentPP;
    }

    /**
     * Retorna los PP máximos del movimiento.
     */
    @Override
    public int maxPP() {
        return maxPP;
    }

    /**
     * Retorna la prioridad del movimiento.
     */
    @Override
    public int priority() {
        return priority;
    }

    /**
     * Aplica el efecto del movimiento al Pokémon objetivo si acierta según su precisión.
     *
     * @param user   el Pokémon que usa el movimiento
     * @param target el Pokémon que recibe el efecto
     */
    @Override
    public void use(Pokemon user, Pokemon target) {
        if (currentPP <= 0 || target == null) return;

        Random rand = new Random();
        if (rand.nextInt(100) < precision) {
            effect.apply(user, target);
        }

        currentPP--;
    }

    /**
     * Establece manualmente los PP restantes del movimiento.
     *
     * @param newPP nuevo valor de PP
     */
    public void setPP(int newPP) {
        this.currentPP = Math.max(0, Math.min(maxPP, newPP));
    }

    /**
     * Devuelve el efecto asociado a este movimiento.
     *
     * @return objeto Effect que representa el efecto a aplicar
     */
    public Effect getEffect() {
        return effect;
    }
}
