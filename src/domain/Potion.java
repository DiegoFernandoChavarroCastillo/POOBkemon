package domain;

public class Potion extends Item {
    public Potion() {
        super("Potion", false, 20);
    }


    @Override
    public void use(Pokemon pokemon) {
        if (pokemon.getHp() > 0) {
            pokemon.heal(HEAL);
        }
    }
}
