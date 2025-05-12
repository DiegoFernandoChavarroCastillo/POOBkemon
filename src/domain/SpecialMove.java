package domain;

import java.util.Random;

/**
 * Movimiento de tipo especial.
 */
public class SpecialMove extends Move {
    private final String name;
    private final String type;
    private final int power;
    private final int precision;
    private final int maxPP;
    private final int priority;
    private int currentPP;

    public SpecialMove(String name, String type, int power, int precision, int maxPP, int priority) {
        this.name = name;
        this.type = type;
        this.power = power;
        this.precision = precision;
        this.maxPP = maxPP;
        this.priority = priority;
        this.currentPP = maxPP;
    }

    public String name() { return name; }
    public String type() { return type; }
    public int power() { return power; }
    public int precision() { return precision; }
    public int pp() { return currentPP; }
    public int maxPP() { return maxPP; }
    public int priority() { return priority; }

    public void use(Pokemon user, Pokemon target) {
        if (currentPP <= 0 || target == null) return;
        if (target.getHp() <= 0) return;

        Random rand = new Random();
        if (rand.nextInt(100) < precision) {
            double multiplier = TypeChart.getEffectiveness(type, target.getType());
            int damage = (int) (((2 * user.getLevel() / 5 + 2) * power * user.getSpecialAttack() / target.getSpecialDefense()) / 50.0 + 2);
            target.takeDamage((int) (damage * multiplier));
            currentPP--;
        }
    }
    public void setPP(int newPP) {
        this.currentPP = Math.max(0, Math.min(maxPP, newPP));
    }

}
