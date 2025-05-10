package domain;

import javax.swing.*;
import java.util.Optional;
import java.util.Random;

/**
 * Clase que maneja la lógica de una batalla Pokémon entre dos entrenadores.
 */
public class Battle {
    private Trainer player1;
    private Trainer player2;
    private int turn;
    private boolean battleEnded;

    private static String currentClimate = null;
    private static int climateDuration = 0;
    private static final Random random = new Random();

    public Battle(Trainer player1, Trainer player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.turn = 1;
        this.battleEnded = false;
    }

    /**
     * Verifica si el Pokémon activo del oponente se ha debilitado
     * y actualiza el estado de la batalla.
     */
    private void checkFaintedPokemon() {
        Trainer opponent = getOpponent();
        Trainer current = getCurrentPlayer();
        Pokemon activeOpponentPokemon = opponent.getActivePokemon();

        if (activeOpponentPokemon != null && activeOpponentPokemon.getHp() <= 0) {
            activeOpponentPokemon.setHp(0);

            if (opponent.getTeam().isAllFainted()) {
                battleEnded = true;
            } else if (opponent.isCPU()) {
                // Lógica para CPU
                int switchIndex = opponent.getTeam().findHealthyPokemon();
                if (switchIndex != -1) {
                    opponent.switchPokemon(switchIndex);
                }
            }

        }
    }

    /**
     * Ejecuta una acción en la batalla
     * @param action Acción a realizar
     */
    public void performAction(Action action) {
        if (battleEnded) {
            throw new IllegalStateException("La batalla ha terminado");
        }

        Trainer current = getCurrentPlayer();

        if (current.isCPU()) {
            throw new IllegalStateException("No se pueden realizar acciones manuales para un CPU");
        }

        executeAction(current, action);
        postAction();
    }

    /**
     * Ejecuta el turno automático de la CPU
     */
    public void executeCpuTurn() {
        if (!battleEnded && getCurrentPlayer().isCPU()) {
            CPUTrainer cpu = (CPUTrainer) getCurrentPlayer();
            Action action = cpu.decideAction();


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            executeAction(cpu, action);
            postAction();
        }
    }

    private void executeAction(Trainer current, Action action) {
        switch (action.getType()) {
            case ATTACK:
                current.attack(action.getMoveIndex(), getOpponent());
                break;
            case USE_ITEM:
                current.useItem(action.getItemIndex(), action.getTargetIndex());
                break;
            case SWITCH_POKEMON:
                current.switchPokemon(action.getTargetIndex());
                break;
        }
    }

    private void postAction() {
        checkFaintedPokemon();
        updateClimate();

        if (battleEnded) {
            return;
        }

        // Solo programar el turno de la CPU si es su turno
        if (getCurrentPlayer().isCPU()) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    executeCpuTurn();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }


    private void updateClimate() {
        if (climateDuration > 0) {
            climateDuration--;
            if (climateDuration == 0) {
                currentClimate = null;
            }
        }
    }

    public void changeTurn() {
        this.turn = 3 - this.turn;
    }

    public boolean isFinished() {
        return battleEnded;
    }

    public Trainer getWinner() {
        if (!battleEnded) return null;

        if (player1.hasAvailablePokemon() && !player2.hasAvailablePokemon()) {
            return player1;
        } else if (player2.hasAvailablePokemon() && !player1.hasAvailablePokemon()) {
            return player2;
        }
        return null; // Empate
    }

    public static void setClimate(String climate, int duration) {
        currentClimate = climate;
        climateDuration = duration;
    }

    public static String getClimate() {
        return currentClimate;
    }

    // Getters
    public Trainer getCurrentPlayer() {
        return turn == 1 ? player1 : player2;
    }

    public Trainer getOpponent() {
        return turn == 1 ? player2 : player1;
    }

    public Trainer getPlayer1() {
        return player1;
    }

    public Trainer getPlayer2() {
        return player2;
    }

    public int getTurn() {
        return turn;
    }

    /**
     * Obtiene el estado actual de la batalla como texto
     */
    public String getBattleStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("Turno de ").append(getCurrentPlayer().getName()).append("\n");
        sb.append(player1.getName()).append(" - ").append(player1.getActivePokemon().getName())
                .append(" [HP: ").append(player1.getActivePokemon().getHp()).append("/")
                .append(player1.getActivePokemon().getMaxHp()).append("]\n");
        sb.append(player2.getName()).append(" - ").append(player2.getActivePokemon().getName())
                .append(" [HP: ").append(player2.getActivePokemon().getHp()).append("/")
                .append(player2.getActivePokemon().getMaxHp()).append("]\n");

        if (currentClimate != null) {
            sb.append("Clima actual: ").append(currentClimate)
                    .append(" (").append(climateDuration).append(" turnos restantes)\n");
        }

        return sb.toString();
    }
}