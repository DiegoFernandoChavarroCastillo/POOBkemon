package domain;

public class SuperPotion extends Item {
    public SuperPotion() {
        super("Super Potion", false, 50);
    }


    @Override
    public void use(Pokemon pokemon) {
        if (pokemon.getHp() > 0) {
            pokemon.heal(HEAL);
        }
    }
}
