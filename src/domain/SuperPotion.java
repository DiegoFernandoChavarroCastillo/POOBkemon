package domain;

import java.io.Serializable;

public class SuperPotion extends Item implements Serializable {
    public SuperPotion() {
        super("Super Potion", false, 50);
    }
    private static final long serialVersionUID = 1L;

    @Override
    public void use(Pokemon pokemon) {
        if (pokemon.getHp() > 0) {
            pokemon.heal(HEAL);
        }
    }
}
