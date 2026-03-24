package domain;

import java.io.Serializable;

/**
 * Representa una poción básica que cura 20 puntos de vida a un Pokémon.
 * Solo puede ser usada si el Pokémon no está debilitado.
 */
public class Potion extends Item implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Crea una nueva instancia de Potion con un valor de curación de 20 HP.
     */
    public Potion() {
        super("Potion", false, 20);
    }

    /**
     * Aplica el efecto de curación al Pokémon si no está debilitado.
     *
     * @param pokemon el Pokémon objetivo
     */
    @Override
    public void use(Pokemon pokemon) {
        if (pokemon.getHp() > 0) {
            pokemon.heal(HEAL);
        }
    }
}
