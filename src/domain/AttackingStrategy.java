package domain;

import java.util.List;
import java.util.Random;

/**
 * Estrategia ofensiva: prioriza movimientos que causan daño o reducen defensa del oponente
 */
public class AttackingStrategy implements BattleStrategy {
    private Random random = new Random();

    @Override
    public Action decideAction(CPUTrainer trainer, Battle battle) {


        Pokemon current = trainer.getActivePokemon();
        Pokemon opponent = battle.getOpponent().getActivePokemon();

        // Primero considerar usar ítem
        Action itemAction = considerUsingItem(trainer, current);
        if (itemAction != null) return itemAction;

        // Solo cambia si la salud es muy baja
        if (current.getHp() < current.getMaxHp() * 0.2) {
            int switchIndex = selectPokemonToSwitch(trainer, opponent);
            if (switchIndex != -1) {
                return Action.createSwitchPokemon(switchIndex);
            }
        }

        // Busca movimientos ofensivos con mayor poder
        List<Move> moves = current.getMoves();
        Move bestMove = null;
        int bestPower = 0;

        for (Move move : moves) {
            if (move.pp() > 0 && move.power() > bestPower) {
                bestMove = move;
                bestPower = move.power();
            }
        }

        if (bestMove != null) {
            return Action.createAttack(moves.indexOf(bestMove));
        }

        return getRandomUsableMove(moves);
    }

    private Action getRandomUsableMove(List<Move> moves) {
        int attempts = 0;
        while (attempts < 10) {
            int index = random.nextInt(moves.size());
            if (moves.get(index).pp() > 0) {
                return Action.createAttack(index);
            }
            attempts++;
        }
        return Action.createAttack(-1); // Struggle
    }
}
