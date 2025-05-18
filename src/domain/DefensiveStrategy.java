package domain;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

/**
 * Estrategia defensiva: prioriza movimientos que mejoran defensa o reducen ataque del oponente
 */
public class DefensiveStrategy implements BattleStrategy, Serializable {
    private Random random = new Random();
    private static final long serialVersionUID = 1L;

    @Override
    public Action decideAction(CPUTrainer trainer, Battle battle) {

        // Mayor prioridad a revivir en estrategia defensiva
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

        // Mayor prioridad a ítems en estrategia defensiva
        if (current.getHp() < current.getMaxHp() * 0.7) {
            Action itemAction = considerUsingItem(trainer, current);
            if (itemAction != null) return itemAction;
        }


        // Si la salud es baja, intenta cambiar
        if (current.getHp() < current.getMaxHp() * 0.3) {
            int switchIndex = selectPokemonToSwitch(trainer, opponent);
            if (switchIndex != -1) {
                return Action.createSwitchPokemon(switchIndex);
            }
        }

        // Busca movimientos defensivos
        List<Move> moves = current.getMoves();
        for (Move move : moves) {
            if (move.pp() > 0 && isDefensiveMove(move)) {
                return Action.createAttack(moves.indexOf(move));
            }
        }

        // Si no encuentra movimientos defensivos, usa uno aleatorio
        return getRandomUsableMove(moves);
    }

    private boolean isDefensiveMove(Move move) {
        // Implementa lógica para identificar movimientos defensivos
        return move.name().contains("Defense") || move.name().contains("Protect")
                || move.name().contains("Barrier") || move.name().contains("Harden");
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