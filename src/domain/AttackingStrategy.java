package domain;

import java.util.List;
import java.util.Random;

/**
 * Implementation of BattleStrategy that prioritizes offensive moves,
 * selecting attacks that deal maximum damage or reduce the opponent's defense.
 * This strategy considers using items and switching Pokémon when health is critical,
 * but primarily focuses on maximizing damage dealt to the opponent.
 *
 */
public class AttackingStrategy implements BattleStrategy {
    private Random random = new Random();

    /**
     * Decides the action to perform during a battle turn, prioritizing
     * offensive moves. Considers using items if beneficial, switching Pokémon
     * when health is critical, or selecting the highest power move available.
     *
     * @param trainer CPU Trainer using this strategy
     * @param battle Current battle context
     * @return Action with the decision made (use item, switch Pokémon, or attack)
     * @see Action#createSwitchPokemon(int)
     * @see Action#createAttack(int)
     */
    @Override
    public Action decideAction(CPUTrainer trainer, Battle battle) {
        Pokemon current = trainer.getActivePokemon();
        Pokemon opponent = battle.getOpponent().getActivePokemon();

        Action itemAction = considerUsingItem(trainer, current);
        if (itemAction != null) return itemAction;

        if (current.getHp() < current.getMaxHp() * 0.2) {
            int switchIndex = selectPokemonToSwitch(trainer, opponent);
            if (switchIndex != -1) {
                return Action.createSwitchPokemon(switchIndex);
            }
        }

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

    /**
     * Randomly selects a usable move (with remaining PP) from the list.
     * Makes up to 10 attempts before returning the Struggle move.
     *
     * @param moves List of available moves
     * @return Action with the index of the selected move or -1 (Struggle)
     * @see Move#pp()
     */
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