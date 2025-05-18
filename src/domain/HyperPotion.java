package domain;

import java.io.Serializable;

/**
 * Representa una poción hiper que cura una gran cantidad de HP a un Pokémon.
 * Solo puede ser usada en Pokémon que no estén debilitados.
 */
public class HyperPotion extends Item implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Crea una nueva instancia de HyperPotion con un valor de curación de 200 HP.
     */
    public HyperPotion() {
        super("Hyper Potion", false, 200);
    }

    /**
     * Aplica el efecto de la HyperPotion al Pokémon dado.
     * Solo cura si el Pokémon no está debilitado.
     *
     * @param pokemon el Pokémon objetivo de la curación
     */
    @Override
    public void use(Pokemon pokemon) {
        if (pokemon.getHp() > 0) {
            pokemon.heal(HEAL);
        }
    }
}
