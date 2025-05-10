package domain;

/**
 * Representa un movimiento que un Pokémon puede usar.
 */
public abstract class Move implements Cloneable {
    public abstract String name();
    public abstract String type();
    public abstract int power();
    public abstract int precision();
    public abstract int pp();
    public abstract int maxPP();
    public abstract int priority();
    public abstract void use(Pokemon user, Pokemon target);
    @Override
    public Move clone() {
        try {
            return (Move) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // No debería ocurrir
        }

    }
}