package domain;

import java.util.List;

/**
 * Interfaz que define la estrategia de comportamiento de un entrenador controlado por la CPU.
 * Proporciona métodos para decidir acciones durante una batalla, cambiar de Pokémon y usar objetos.
 */
public interface BattleStrategy {

    /**
     * Decide la acción que debe realizar el entrenador CPU durante su turno.
     *
     * @param trainer Entrenador CPU que realiza la acción
     * @param battle Contexto actual de la batalla
     * @return Acción que debe ejecutar el entrenador
     */
    Action decideAction(CPUTrainer trainer, Battle battle);

    /**
     * Selecciona el mejor Pokémon del equipo para hacer un cambio estratégico.
     * Evalúa la efectividad potencial y la vida restante de cada Pokémon.
     *
     * @param trainer Entrenador CPU que realiza el cambio
     * @param opponentPokemon Pokémon activo del oponente
     * @return Índice del Pokémon a cambiar, o -1 si no hay opción viable
     */
    default int selectPokemonToSwitch(CPUTrainer trainer, Pokemon opponentPokemon) {
        List<Pokemon> team = trainer.getTeam().getPokemons();
        int bestIndex = -1;
        double bestScore = -1;

        for (int i = 0; i < team.size(); i++) {
            Pokemon p = team.get(i);
            if (p.getHp() > 0 && p != trainer.getActivePokemon()) {
                double score = calculateEffectiveness(p, opponentPokemon) * (p.getHp() / (double) p.getMaxHp());
                if (score > bestScore) {
                    bestScore = score;
                    bestIndex = i;
                }
            }
        }
        return bestIndex;
    }

    /**
     * Evalúa si el entrenador CPU debería usar un objeto en el turno actual.
     * Intenta revivir Pokémon debilitados o curar al Pokémon activo si es necesario.
     *
     * @param trainer Entrenador CPU que podría usar un objeto
     * @param currentPokemon Pokémon activo del entrenador
     * @return Acción para usar un objeto, o {@code null} si no se recomienda ningún uso
     */
    default Action considerUsingItem(CPUTrainer trainer, Pokemon currentPokemon) {
        if (trainer.getItems().isEmpty()) return null;

        List<Pokemon> team = trainer.getTeam().getPokemons();
        int currentIndex = team.indexOf(currentPokemon);

        for (int i = 0; i < team.size(); i++) {
            Pokemon p = team.get(i);
            if (p.getHp() <= 0) {
                for (int j = 0; j < trainer.getItems().size(); j++) {
                    Item item = trainer.getItems().get(j);
                    if (item instanceof Revive) {
                        return Action.createUseItem(j, i);
                    }
                }
            }
        }

        if (currentPokemon.getHp() > 0 && currentPokemon.getHp() < currentPokemon.getMaxHp()) {
            if (currentPokemon.getHp() < currentPokemon.getMaxHp() * 0.3) {
                for (int i = 0; i < trainer.getItems().size(); i++) {
                    Item item = trainer.getItems().get(i);
                    if (item instanceof HyperPotion) {
                        return Action.createUseItem(i, currentIndex);
                    }
                }
            }

            if (currentPokemon.getHp() < currentPokemon.getMaxHp() * 0.5) {
                for (int i = 0; i < trainer.getItems().size(); i++) {
                    Item item = trainer.getItems().get(i);
                    if (item instanceof SuperPotion || item instanceof Potion) {
                        return Action.createUseItem(i, currentIndex);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Calcula la efectividad de un Pokémon atacante contra un defensor.
     * Este método puede ser sobreescrito con una lógica más precisa según el sistema de tipos.
     *
     * @param attacker Pokémon que atacaría
     * @param defender Pokémon objetivo del ataque
     * @return Valor de efectividad (por defecto, 1.0)
     */
    default double calculateEffectiveness(Pokemon attacker, Pokemon defender) {
        return 1.0; // Implementación por defecto
    }
}
