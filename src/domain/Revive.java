package domain;

public class Revive extends Item {
    public Revive() {
        super("Revive", true, 0);
    }


    @Override
    public void use(Pokemon pokemon) {
        if (pokemon.getHp() == 0) {
            int halfHP = pokemon.getMaxHp() / 2;
            pokemon.revive(halfHP);
        }
    }
}
