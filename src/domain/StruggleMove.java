package domain;

public class StruggleMove extends PhysicalMove {
    public StruggleMove() {
        super("STRUGGLE", "NORMAL", 50, 100, Integer.MAX_VALUE, 0);
    }

    @Override
    public void use(Pokemon user, Pokemon target) {
        if (target == null || target.getHp() <= 0) return;

        int damage = power();
        target.takeDamage(damage);
        user.takeDamage(damage / 2);
    }
}