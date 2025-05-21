package domain;

import java.io.Serializable;
import java.util.List;

/**
 * Estrategia de batalla para un entrenador CPU que prioriza cambiar de Pokémon
 * si hay uno en el equipo con mayor efectividad contra el oponente actual.
 * También puede usar objetos si el Pokémon activo está herido.
 */
public class ChangingStrategy implements BattleStrategy, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Decide la acción del entrenador CPU según esta estrategia.
     * Intenta usar un objeto si el Pokémon está herido, luego evalúa si conviene cambiar
     * a otro Pokémon más efectivo contra el oponente. Si no, ataca normalmente.
     *
     * @param trainer Entrenador CPU que decide la acción
     * @param battle Estado actual de la batalla
     * @return Acción que debe ejecutar el entrenador
     */
    @Override
    public Action decideAction(CPUTrainer trainer, Battle battle) {
        Pokemon current = trainer.getActivePokemon();
        Pokemon opponent = battle.getOpponent().getActivePokemon();

        // Considerar uso de ítem si el Pokémon activo está herido
        if (current.getHp() < current.getMaxHp() * 0.5) {
            Action itemAction = considerUsingItem(trainer, current);
            if (itemAction != null) return itemAction;
        }

        double currentEffectiveness = calculateEffectiveness(current, opponent);

        // Evaluar si hay un Pokémon con mejor efectividad para cambiar
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

        // Cambiar si se encuentra un Pokémon significativamente más efectivo
        if (bestIndex != -1 && bestEffectiveness > currentEffectiveness * 1.2) {
            return Action.createSwitchPokemon(bestIndex);
        }

        // En caso contrario, usar estrategia de ataque por defecto
        return new AttackingStrategy().decideAction(trainer, battle);
    }
}
