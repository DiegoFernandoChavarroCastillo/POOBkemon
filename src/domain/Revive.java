package domain;

import java.io.Serializable;

/**
 * Representa un ítem que revive a un Pokémon debilitado con la mitad de sus puntos de vida.
 */
public class Revive extends Item implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Crea una nueva instancia de Revive.
     */
    public Revive() {
        super("Revive", true, 0);
    }

    /**
     * Revive al Pokémon si está debilitado, restaurando el 50% de su HP máximo.
     *
     * @param pokemon el Pokémon objetivo
     */
    @Override
    public void use(Pokemon pokemon) {
        if (pokemon.getHp() == 0) {
            int halfHP = pokemon.getMaxHp() / 2;
            pokemon.revive(halfHP);
        }
    }
}
