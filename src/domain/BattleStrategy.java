package domain;

import java.util.List;

/**
 * Interfaz que define la estrategia de comportamiento de un entrenador CPU
 */
public interface BattleStrategy {
    Action decideAction(CPUTrainer trainer, Battle battle);

    /**
     * Método para seleccionar un Pokémon cuando es necesario cambiar
     */
    default int selectPokemonToSwitch(CPUTrainer trainer, Pokemon opponentPokemon) {
        List<Pokemon> team = trainer.getTeam().getPokemons();
        int bestIndex = -1;
        double bestScore = -1;

        for (int i = 0; i < team.size(); i++) {
            Pokemon p = team.get(i);
            if (p.getHp() > 0 && p != trainer.getActivePokemon()) {
                // Puntaje basado en efectividad y salud
                double score = calculateEffectiveness(p, opponentPokemon) * (p.getHp() / (double)p.getMaxHp());
                if (score > bestScore) {
                    bestScore = score;
                    bestIndex = i;
                }
            }
        }
        return bestIndex;
    }

    default Action considerUsingItem(CPUTrainer trainer, Pokemon currentPokemon) {
        if (trainer.getItems().isEmpty()) return null;

        List<Pokemon> team = trainer.getTeam().getPokemons();
        int currentIndex = team.indexOf(currentPokemon);

        // 1. Revivir SOLO si el Pokémon está debilitado (HP == 0)
        for (int i = 0; i < team.size(); i++) {
            Pokemon p = team.get(i);
            if (p.getHp() <= 0) {  // Pokémon está debilitado
                for (int j = 0; j < trainer.getItems().size(); j++) {
                    Item item = trainer.getItems().get(j);
                    if (item instanceof Revive) {
                        // Debug para verificar
                        System.out.println("[DEBUG] Usando Revive en " + p.getName() + " (HP: " + p.getHp() + ")");
                        return Action.createUseItem(j, i);
                    }
                }
            }
        }

        // 2. Usar pociones SOLO si el Pokémon tiene vida (HP > 0) pero está herido
        if (currentPokemon.getHp() > 0 && currentPokemon.getHp() < currentPokemon.getMaxHp()) {
            // Priorizar pociones más fuertes primero
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
     * Calcula la efectividad de un Pokémon contra otro
     */
    default double calculateEffectiveness(Pokemon attacker, Pokemon defender) {
        // Implementación simplificada - deberías usar tu sistema de tipos real
        return 1.0; // Valor por defecto, implementa la lógica real según tus tipos
    }
}