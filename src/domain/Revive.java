package domain;

import java.io.Serializable;

public class Revive extends Item implements Serializable {
    public Revive() {
        super("Revive", true, 0);
    }
    private static final long serialVersionUID = 1L;

    @Override
    public void use(Pokemon pokemon) {
        if (pokemon.getHp() == 0) {
            int halfHP = pokemon.getMaxHp() / 2;
            pokemon.revive(halfHP);
        }
    }
}
