package domain;

import java.io.Serializable;

public class Potion extends Item implements Serializable {
    public Potion() {
        super("Potion", false, 20);
    }
    private static final long serialVersionUID = 1L;

    @Override
    public void use(Pokemon pokemon) {
        if (pokemon.getHp() > 0) {
            pokemon.heal(HEAL);
        }
    }
}
