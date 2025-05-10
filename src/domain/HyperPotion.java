package domain;

public class HyperPotion extends Item {
    public HyperPotion() {
        super("Hyper Potion", false, 200);
    }

    @Override
    public void use(Pokemon pokemon) {
        if (pokemon.getHp() > 0) {
            pokemon.heal(HEAL);
        }
    }
}
