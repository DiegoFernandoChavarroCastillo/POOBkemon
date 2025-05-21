package domain;

import java.io.Serializable;
import java.util.Random;

/**
 * Representa un movimiento de estado que aplica un efecto sin causar daño directo.
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
     * @param name       nombre del movimiento
     * @param type       tipo del movimiento
     * @param precision  precisión del movimiento (0–100)
     * @param maxPP      puntos de poder máximos
     * @param priority   prioridad del movimiento
     * @param effect     efecto que se aplicará al usarse
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

    @Override
    public String name() {
        return name;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public int power() {
        return 0; // No causa daño directo
    }

    @Override
    public int precision() {
        return precision;
    }

    @Override
    public int pp() {
        return currentPP;
    }

    @Override
    public int maxPP() {
        return maxPP;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public void use(Pokemon user, Pokemon target) {
        if (currentPP <= 0 || target == null) return;

        Random rand = new Random();
        if (rand.nextInt(100) < precision) {
            effect.apply(user, target);
        }

        currentPP--;
    }

    public void setPP(int newPP) {
        this.currentPP = Math.max(0, Math.min(maxPP, newPP));
    }

    public Effect getEffect() {
        return effect;
    }
}
