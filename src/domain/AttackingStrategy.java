package domain;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

/**
 * Estrategia de batalla que prioriza los movimientos ofensivos,
 * seleccionando ataques que inflijan el mayor daño posible o reduzcan la defensa del oponente.
 * Considera el uso de objetos o el cambio de Pokémon si la salud es crítica,
 * pero su enfoque principal es maximizar el daño causado al rival.
 */
public class AttackingStrategy implements BattleStrategy, Serializable {
    private Random random = new Random();
    private static final long serialVersionUID = 1L;

    /**
     * Decide la acción a realizar durante un turno de batalla, priorizando los ataques ofensivos.
     * Evalúa primero si conviene usar un objeto, luego si es necesario cambiar de Pokémon
     * por salud crítica, y finalmente elige el movimiento con mayor poder disponible.
     *
     * @param trainer el entrenador CPU que usa esta estrategia
     * @param battle el contexto actual de la batalla
     * @return una instancia de {@code Action} con la decisión tomada
     * @see Action#createSwitchPokemon(int)
     * @see Action#createAttack(int)
     */
    @Override
    public Action decideAction(CPUTrainer trainer, Battle battle) {
        Pokemon actual = trainer.getActivePokemon();
        Pokemon oponente = battle.getOpponent().getActivePokemon();

        Action accionObjeto = considerUsingItem(trainer, actual);
        if (accionObjeto != null) return accionObjeto;

        if (actual.getHp() < actual.getMaxHp() * 0.2) {
            int indiceCambio = selectPokemonToSwitch(trainer, oponente);
            if (indiceCambio != -1) {
                return Action.createSwitchPokemon(indiceCambio);
            }
        }

        List<Move> movimientos = actual.getMoves();
        Move mejorMovimiento = null;
        int mayorPoder = 0;

        for (Move movimiento : movimientos) {
            if (movimiento.pp() > 0 && movimiento.power() > mayorPoder) {
                mejorMovimiento = movimiento;
                mayorPoder = movimiento.power();
            }
        }

        if (mejorMovimiento != null) {
            return Action.createAttack(movimientos.indexOf(mejorMovimiento));
        }

        return getRandomUsableMove(movimientos);
    }

    /**
     * Selecciona aleatoriamente un movimiento utilizable (con PP disponibles) desde la lista.
     * Intenta hasta 10 veces encontrar un movimiento válido antes de retornar el movimiento Forcejeo.
     *
     * @param moves lista de movimientos disponibles
     * @return una instancia de {@code Action} con el índice del movimiento elegido o -1 (Forcejeo)
     * @see Move#pp()
     */
    private Action getRandomUsableMove(List<Move> moves) {
        int intentos = 0;
        while (intentos < 10) {
            int indice = random.nextInt(moves.size());
            if (moves.get(indice).pp() > 0) {
                return Action.createAttack(indice);
            }
            intentos++;
        }
        return Action.createAttack(-1);
    }
}
