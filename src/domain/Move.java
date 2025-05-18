package domain;

import java.io.Serializable;

/**
 * Representa un movimiento que un Pokémon puede usar en combate.
 */
public abstract class Move implements Cloneable, Serializable {

    /**
     * @return Nombre del movimiento.
     */
    public abstract String name();

    /**
     * @return Tipo del movimiento (por ejemplo, Fuego, Agua, etc.).
     */
    public abstract String type();

    /**
     * @return Poder base del movimiento.
     */
    public abstract int power();

    /**
     * @return Precisión del movimiento (0–100).
     */
    public abstract int precision();

    /**
     * @return PP actuales del movimiento.
     */
    public abstract int pp();

    /**
     * @return PP máximos del movimiento.
     */
    public abstract int maxPP();

    /**
     * @return Prioridad del movimiento en la secuencia del turno.
     */
    public abstract int priority();

    /**
     * Aplica el efecto del movimiento entre el Pokémon usuario y el objetivo.
     *
     * @param user   Pokémon que usa el movimiento
     * @param target Pokémon objetivo del movimiento
     */
    public abstract void use(Pokemon user, Pokemon target);

    /**
     * Clona el movimiento actual.
     *
     * @return una copia exacta del movimiento
     */
    @Override
    public Move clone() {
        try {
            return (Move) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // No debería ocurrir
        }
    }
}
