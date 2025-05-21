package domain;

import java.io.Serializable;

/**
 * Representa una superpoción que cura 50 puntos de vida a un Pokémon.
 * Solo puede usarse si el Pokémon no está debilitado.
 */
public class SuperPotion extends Item implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Crea una nueva instancia de SuperPotion con un valor de curación de 50 HP.
     */
    public SuperPotion() {
        super("Super Potion", false, 50);
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
