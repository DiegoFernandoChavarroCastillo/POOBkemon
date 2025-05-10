package domain;

/**
 * Movimiento de emergencia cuando no quedan PP.
 */
public class Struggle extends Move {
    public String name() { return "Struggle"; }
    public String type() { return "Normal"; }
    public int power() { return 50; }
    public int precision() { return 100; }
    public int pp() { return Integer.MAX_VALUE; }
    public int maxPP() { return Integer.MAX_VALUE; }
    public int priority() { return 0; }

    public void use(Pokemon user, Pokemon target) {
        if (target == null || target.getHp() <= 0) return;
        target.takeDamage(power());
        user.takeDamage(user.getHp() / 4); // daño de retroceso
    }
}