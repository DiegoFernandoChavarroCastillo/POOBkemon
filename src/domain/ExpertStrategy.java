package domain;

import java.util.List;

/**
 * Estrategia experta: combina diferentes enfoques según la situación
 */
public class ExpertStrategy implements BattleStrategy {
    @Override
    public Action decideAction(CPUTrainer trainer, Battle battle) {
        Pokemon current = trainer.getActivePokemon();
        Pokemon opponent = battle.getOpponent().getActivePokemon();

        // 1. Prioridad absoluta: Revivir Pokémon debilitados
        List<Pokemon> team = trainer.getTeam().getPokemons();
        for (int i = 0; i < team.size(); i++) {
            Pokemon p = team.get(i);
            if (p.getHp() <= 0) {
                for (int j = 0; j < trainer.getItems().size(); j++) {
                    if (trainer.getItems().get(j) instanceof Revive) {
                        return Action.createUseItem(j, i); // Revive al Pokémon debilitado
                    }
                }
            }
        }

        // 2. Si el Pokémon activo está debilitado, cambiar
        if (current.getHp() <= 0) {
            int switchIndex = selectPokemonToSwitch(trainer, opponent);
            if (switchIndex != -1) {
                return Action.createSwitchPokemon(switchIndex);
            }
            return Action.createAttack(-1); // Struggle como último recurso
        }

        // Estrategia inteligente para ítems
        if (current.getHp() < current.getMaxHp() * 0.4 ||
                (current.getHp() < current.getMaxHp() * 0.6 && battle.getOpponent().getActivePokemon().getHp() > current.getHp())) {
            Action itemAction = considerUsingItem(trainer, current);
            if (itemAction != null) return itemAction;
        }


        // 1. Si la salud es crítica, prioriza cambiar o usar items
        if (current.getHp() < current.getMaxHp() * 0.2) {
            // Intenta cambiar
            int switchIndex = selectPokemonToSwitch(trainer, opponent);
            if (switchIndex != -1) {
                return Action.createSwitchPokemon(switchIndex);
            }

            // Si no puede cambiar, usa un item si tiene
            if (!trainer.getItems().isEmpty()) {
                // Implementar lógica para seleccionar mejor item
                return Action.createUseItem(0, trainer.getTeam().getPokemons().indexOf(current));
            }
        }

        // 2. Selecciona el mejor movimiento considerando efectividad y poder
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

        // 3. Si no hay movimientos disponibles, cambia
        int switchIndex = selectPokemonToSwitch(trainer, opponent);
        if (switchIndex != -1) {
            return Action.createSwitchPokemon(switchIndex);
        }

        // 4. Último recurso
        return Action.createAttack(-1); // Struggle
    }

    private double calculateEffectiveness(Pokemon attacker, Pokemon defender, Move move) {
        // Implementa lógica avanzada de efectividad considerando el movimiento
        return 1.0; // Valor por defecto
    }
}