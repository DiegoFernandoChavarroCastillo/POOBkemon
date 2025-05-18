package domain;

import java.io.Serializable;

public class StruggleMove extends PhysicalMove implements Serializable {
    public StruggleMove() {
        super("STRUGGLE", "NORMAL", 50, 100, Integer.MAX_VALUE, 0);
    }
    private static final long serialVersionUID = 1L;
    @Override
    public void use(Pokemon user, Pokemon target) {
        if (target == null || target.getHp() <= 0) return;

        int damage = power();
        target.takeDamage(damage);
        user.takeDamage(damage / 2);
    }
}