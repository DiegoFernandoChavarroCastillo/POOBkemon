package domain;

import java.io.Serializable;

public class HyperPotion extends Item implements Serializable {
    public HyperPotion() {
        super("Hyper Potion", false, 200);
    }
    private static final long serialVersionUID = 1L;
    @Override
    public void use(Pokemon pokemon) {
        if (pokemon.getHp() > 0) {
            pokemon.heal(HEAL);
        }
    }
}
