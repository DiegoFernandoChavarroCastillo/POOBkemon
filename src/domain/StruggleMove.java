package domain;

import java.io.Serializable;

/**
 * Representa el movimiento forzado 'Struggle', usado cuando el Pokémon no tiene PP disponibles.
 * Causa daño al objetivo y retroceso al usuario.
 */
public class StruggleMove extends PhysicalMove implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Crea una instancia de Struggle con poder fijo y PP ilimitado.
     */
    public StruggleMove() {
        super("STRUGGLE", "NORMAL", 50, 100, Integer.MAX_VALUE, 0);
    }

    /**
     * Aplica daño al objetivo y luego inflige daño por retroceso al usuario.
     *
     * @param user   Pokémon que usa Struggle
     * @param target Pokémon objetivo
     */
    @Override
    public void use(Pokemon user, Pokemon target) {
        if (target == null || target.getHp() <= 0) return;

        int damage = power();
        target.takeDamage(damage);
        user.takeDamage(damage / 2);
    }
}
