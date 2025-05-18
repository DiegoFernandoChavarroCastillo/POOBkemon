package domain;

import java.io.Serializable;
import java.util.List;

/**
 * Estrategia experta utilizada por entrenadores controlados por la CPU.
 * Combina tácticas ofensivas, defensivas y de soporte para tomar decisiones óptimas
 * según la situación del combate.
 */
public class ExpertStrategy implements BattleStrategy, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Decide la acción a realizar por el entrenador en función del estado del combate.
     * La estrategia prioriza revivir aliados, utilizar objetos, cambiar Pokémon debilitados
     * y seleccionar el movimiento más efectivo.
     *
     * @param trainer Entrenador que ejecuta la estrategia
     * @param battle  Batalla actual en la que se toma la decisión
     * @return Acción determinada a ejecutar
     */
    @Override
    public Action decideAction(CPUTrainer trainer, Battle battle) {
        Pokemon current = trainer.getActivePokemon();
        Pokemon opponent = battle.getOpponent().getActivePokemon();

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

        if (current.getHp() <= 0) {
            int switchIndex = selectPokemonToSwitch(trainer, opponent);
            if (switchIndex != -1) {
                return Action.createSwitchPokemon(switchIndex);
            }
            return Action.createAttack(-1);
        }

        if (current.getHp() < current.getMaxHp() * 0.4 ||
                (current.getHp() < current.getMaxHp() * 0.6 && opponent.getHp() > current.getHp())) {
            Action itemAction = considerUsingItem(trainer, current);
            if (itemAction != null) return itemAction;
        }

        if (current.getHp() < current.getMaxHp() * 0.2) {
            int switchIndex = selectPokemonToSwitch(trainer, opponent);
            if (switchIndex != -1) {
                return Action.createSwitchPokemon(switchIndex);
            }

            if (!trainer.getItems().isEmpty()) {
                return Action.createUseItem(0, trainer.getTeam().getPokemons().indexOf(current));
            }
        }

        List<Move> moves = current.getMoves();
        Move bestMove = null;
        double bestScore = -1;

        for (Move move : moves) {
            if (move.pp() > 0) {
                double effectiveness = calculateEffectiveness(current, opponent, move);
                double score = effectiveness * move.power();

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
        }

        if (bestMove != null) {
            return Action.createAttack(moves.indexOf(bestMove));
        }

        int switchIndex = selectPokemonToSwitch(trainer, opponent);
        if (switchIndex != -1) {
            return Action.createSwitchPokemon(switchIndex);
        }

        return Action.createAttack(-1);
    }

    /**
     * Calcula la efectividad estimada de un movimiento considerando el atacante,
     * el defensor y el movimiento utilizado.
     *
     * @param attacker Pokémon atacante
     * @param defender Pokémon defensor
     * @param move     Movimiento que se desea evaluar
     * @return Valor numérico representando la efectividad (1.0 por defecto)
     */
    private double calculateEffectiveness(Pokemon attacker, Pokemon defender, Move move) {
        return 1.0;
    }
}
