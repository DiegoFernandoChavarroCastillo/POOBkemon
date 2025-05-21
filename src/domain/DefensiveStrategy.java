package domain;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

/**
 * Estrategia defensiva utilizada por entrenadores CPU. Esta estrategia prioriza el uso
 * de objetos curativos, revivir Pokémon debilitados, cambiar de Pokémon cuando la salud
 * es baja y utilizar movimientos defensivos. En caso de no encontrar una acción defensiva
 * viable, ejecuta un movimiento al azar.
 */
public class DefensiveStrategy implements BattleStrategy, Serializable {
    private Random random = new Random();
    private static final long serialVersionUID = 1L;

    /**
     * Decide la acción a realizar por parte del entrenador CPU durante el turno actual.
     *
     * @param trainer Entrenador CPU que ejecutará la acción
     * @param battle Instancia actual de la batalla
     * @return Acción que el entrenador debe realizar en este turno
     */
    @Override
    public Action decideAction(CPUTrainer trainer, Battle battle) {
        List<Pokemon> team = trainer.getTeam().getPokemons();

        for (int i = 0; i < team.size(); i++) {
            Pokemon p = team.get(i);
            if (p.getHp() <= 0) {
                for (int j = 0; j < trainer.getItems().size(); j++) {
                    if (trainer.getItems().get(j) instanceof Revive) {
                        return Action.createUseItem(j, i);
                    }
                }
            }
        }

        Pokemon current = trainer.getActivePokemon();
        Pokemon opponent = battle.getOpponent().getActivePokemon();

        if (current.getHp() < current.getMaxHp() * 0.7) {
            Action itemAction = considerUsingItem(trainer, current);
            if (itemAction != null) return itemAction;
        }

        if (current.getHp() < current.getMaxHp() * 0.3) {
            int switchIndex = selectPokemonToSwitch(trainer, opponent);
            if (switchIndex != -1) {
                return Action.createSwitchPokemon(switchIndex);
            }
        }

        List<Move> moves = current.getMoves();
        for (Move move : moves) {
            if (move.pp() > 0 && isDefensiveMove(move)) {
                return Action.createAttack(moves.indexOf(move));
            }
        }

        return getRandomUsableMove(moves);
    }

    /**
     * Determina si un movimiento es considerado defensivo.
     *
     * @param move Movimiento a evaluar
     * @return true si el movimiento es defensivo, false en caso contrario
     */
    private boolean isDefensiveMove(Move move) {
        return move.name().contains("Defense") || move.name().contains("Protect")
                || move.name().contains("Barrier") || move.name().contains("Harden");
    }

    /**
     * Selecciona un movimiento aleatorio entre los que aún tienen puntos de poder (PP).
     *
     * @param moves Lista de movimientos disponibles
     * @return Acción que representa el uso de un movimiento aleatorio válido
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
