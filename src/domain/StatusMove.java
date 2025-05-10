package domain;

/**
 * Movimiento de tipo de estado (buffs/debuffs).
 */
public class StatusMove extends Move {
    private final String name;
    private final String type;
    private final int precision;
    private final int maxPP;
    private final int priority;
    private final Effect effect;
    private int currentPP;

    public StatusMove(String name, String type, int precision, int maxPP, int priority, Effect effect) {
        this.name = name;
        this.type = type;
        this.precision = precision;
        this.maxPP = maxPP;
        this.priority = priority;
        this.effect = effect;
        this.currentPP = maxPP;
    }

    public String name() { return name; }
    public String type() { return type; }
    public int power() { return 0; }
    public int precision() { return precision; }
    public int pp() { return currentPP; }
    public int maxPP() { return maxPP; }
    public int priority() { return priority; }

    public void use(Pokemon user, Pokemon target) {
        if (currentPP <= 0 || target == null || target.getHp() <= 0) return;

    }
}
