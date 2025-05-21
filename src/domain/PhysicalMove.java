package domain;

import java.io.Serializable;
import java.util.Random;

/**
 * Representa un movimiento físico que causa daño basado en el ataque del usuario y la defensa del objetivo.
 */
public class PhysicalMove extends Move implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String type;
    private final int power;
    private final int precision;
    private final int maxPP;
    private final int priority;
    private int currentPP;

    /**
     * Crea un nuevo movimiento físico con los parámetros especificados.
     *
     * @param name       nombre del movimiento
     * @param type       tipo del movimiento
     * @param power      poder base del movimiento
     * @param precision  precisión (0–100)
     * @param maxPP      puntos de poder máximos
     * @param priority   prioridad del movimiento
     */
    public PhysicalMove(String name, String type, int power, int precision, int maxPP, int priority) {
        this.name = name;
        this.type = type;
        this.power = power;
        this.precision = precision;
        this.maxPP = maxPP;
        this.priority = priority;
        this.currentPP = maxPP;
    }

    @Override
    public String name() { return name; }

    @Override
    public String type() { return type; }

    @Override
    public int power() { return power; }

    @Override
    public int precision() { return precision; }

    @Override
    public int pp() { return currentPP; }

    @Override
    public int maxPP() { return maxPP; }

    @Override
    public int priority() { return priority; }

    /**
     * Aplica el movimiento físico al objetivo si cumple condiciones de precisión y vida.
     *
     * @param user   el Pokémon que usa el movimiento
     * @param target el Pokémon objetivo
     */
    @Override
    public void use(Pokemon user, Pokemon target) {
        if (currentPP <= 0 || target == null) return;
        if (target.getHp() <= 0) return;

        Random rand = new Random();
        if (rand.nextInt(100) < precision) {
            double multiplier = TypeChart.getEffectiveness(type, target.getType());
            int damage = (int) (((2 * user.getLevel() / 5 + 2) * power * user.getAttack() / target.getDefense()) / 50.0 + 2);
            target.takeDamage((int) (damage * multiplier));
            currentPP--;
        }
    }

    /**
     * Establece el valor actual de PP asegurando que esté dentro de los límites válidos.
     *
     * @param newPP nuevo valor de PP
     */
    public void setPP(int newPP) {
        this.currentPP = Math.max(0, Math.min(maxPP, newPP));
    }
}
