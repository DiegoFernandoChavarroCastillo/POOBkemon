package domain;

import java.io.Serial;
import java.io.Serializable;

/**
 * Representa el movimiento especial "Curse" (Maldición), cuyo efecto depende del tipo del usuario.
 * <ul>
 *     <li>Si el usuario <b>no es de tipo Fantasma</b>: aumenta su ataque y defensa, pero reduce su velocidad.</li>
 *     <li>Si el usuario <b>es de tipo Fantasma</b>: pierde la mitad de su HP actual y maldice al objetivo.</li>
 * </ul>
 */
public class CurseMove extends StatusMove implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Construye el movimiento "CURSE" con tipo Fantasma, precisión 100 y 10 PP.
     */
    public CurseMove() {
        super("CURSE", "GHOST", 100, 10, 0, null);
    }

    /**
     * Aplica los efectos del movimiento según el tipo del usuario.
     * Si no es tipo Fantasma, aplica un BUFF al usuario. Si es tipo Fantasma, sacrifica la mitad de su HP y maldice al objetivo.
     *
     * @param user   el Pokémon que usa el movimiento
     * @param target el Pokémon objetivo
     */
    @Override
    public void use(Pokemon user, Pokemon target) {
        if (pp() <= 0 || target == null || target.getHp() <= 0) return;

        setPP(pp() - 1);

        if (!"GHOST".equalsIgnoreCase(user.getType())) {
            // No es tipo fantasma: +1 ataque, +1 defensa, -1 velocidad
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
            // Tipo fantasma: pierde mitad del HP y maldice al objetivo
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

    /**
     * Devuelve una nueva instancia de CurseMove.
     *
     * @return una copia de este movimiento
     */
    @Override
    public Move clone() {
        return new CurseMove();
    }
}
