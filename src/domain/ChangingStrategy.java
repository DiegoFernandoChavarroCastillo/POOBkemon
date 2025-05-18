package domain;

import java.io.Serializable;
import java.util.List;

/**
 * Estrategia de cambio: prioriza cambiar al Pokémon más efectivo contra el oponente
 */
public class ChangingStrategy implements BattleStrategy, Serializable {
    private static final long serialVersionUID = 1L;
    @Override
    public Action decideAction(CPUTrainer trainer, Battle battle) {
        Pokemon current = trainer.getActivePokemon();
        Pokemon opponent = battle.getOpponent().getActivePokemon();

        // Considerar ítem antes de cambiar
        if (current.getHp() < current.getMaxHp() * 0.5) {
            Action itemAction = considerUsingItem(trainer, current);
            if (itemAction != null) return itemAction;
        }

        // Calcula efectividad actual
        double currentEffectiveness = calculateEffectiveness(current, opponent);

        // Busca un Pokémon con mejor efectividad
        int bestIndex = -1;
        double bestEffectiveness = currentEffectiveness;

        List<Pokemon> team = trainer.getTeam().getPokemons();
        for (int i = 0; i < team.size(); i++) {
            Pokemon p = team.get(i);
            if (p.getHp() > 0 && p != current) {
                double effectiveness = calculateEffectiveness(p, opponent);
                if (effectiveness > bestEffectiveness) {
                    bestEffectiveness = effectiveness;
                    bestIndex = i;
                }
            }
        }

        // Si encuentra uno mejor, cambia
        if (bestIndex != -1 && bestEffectiveness > currentEffectiveness * 1.2) {
            return Action.createSwitchPokemon(bestIndex);
        }

        // Si no, usa un ataque normal
        return new AttackingStrategy().decideAction(trainer, battle);
    }
}