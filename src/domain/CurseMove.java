package domain;

import java.io.Serial;
import java.io.Serializable;

public class CurseMove extends StatusMove implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public CurseMove() {
        super("CURSE", "GHOST", 100, 10, 0, null);
    }

    @Override
    public void use(Pokemon user, Pokemon target) {
        if (pp() <= 0 || target == null || target.getHp() <= 0) return;

        setPP(pp() - 1);

        if (!"GHOST".equalsIgnoreCase(user.getType())) {
            // No es tipo fantasma: +1 attack, +1 defense, -1 speed
            Effect buff = new Effect(
                    EffectType.BUFF,
                    Target.USER,
                    new java.util.HashMap<>() {{
                        put("attack", 1);
                        put("defense", 1);
                        put("speed", -1);
                    }},
                    null,
                    999,
                    true,
                    false
            );
            buff.apply(user, target);
        } else {
            // Es tipo fantasma: pierde mitad HP, maldice al enemigo
            int halfHp = user.getHp() / 2;
            user.takeDamage(halfHp);

            Effect curse = new Effect(
                    EffectType.STATUS,
                    Target.OPPONENT,
                    null,
                    "cursed",
                    999,
                    false,
                    false
            );
            curse.apply(user, target);
        }
    }

    @Override
    public Move clone() {
        return new CurseMove();
    }
}