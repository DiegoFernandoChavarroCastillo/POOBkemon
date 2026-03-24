package domain;

import java.io.Serializable;

/**
 * Clase que gestiona la lógica de una batalla Pokémon entre dos entrenadores.
 * Administra el flujo de turnos, la ejecución de acciones, la verificación de estados
 * y las condiciones climáticas durante la batalla.
 */
public class Battle implements Serializable {
    private Trainer player1;
    private Trainer player2;
    private int turn;
    private boolean battleEnded;
    private static String currentClimate = null;
    private static int climateDuration = 0;
    private static final long serialVersionUID = 1L;

    /**
     * Crea una nueva batalla entre dos entrenadores.
     *
     * @param player1 Primer entrenador
     * @param player2 Segundo entrenador
     */
    public Battle(Trainer player1, Trainer player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.turn = 1;
        this.battleEnded = false;
    }

    /**
     * Obtiene el estado actual de la batalla.
     *
     * @return Un objeto {@code BattleState} con la información de la batalla
     */
    public BattleState getBattleState() {
        return new BattleState(
                player1.getName(),
                player2.getName(),
                player1.getActivePokemon(),
                player2.getActivePokemon(),
                turn == 1,
                !getCurrentPlayer().isCPU(),
                currentClimate
        );
    }

    /**
     * Ejecuta una acción realizada por el jugador actual.
     *
     * @param action Acción a ejecutar
     * @throws IllegalStateException si la batalla ya terminó o si el jugador actual es una CPU
     */
    public void performAction(Action action) {
        if (battleEnded) {
            throw new IllegalStateException("La batalla ha terminado");
        }

        Trainer current = getCurrentPlayer();
        if (current.isCPU()) {
            throw new IllegalStateException("No se pueden ejecutar acciones manuales para una CPU");
        }

        processTurnStartEffects();
        executeAction(current, action);
        postAction();
    }

    /**
     * Ejecuta el turno automático si el jugador actual es una CPU.
     */
    public void executeCpuTurn() {
        if (!battleEnded && getCurrentPlayer().isCPU()) {
            processTurnStartEffects();
            CPUTrainer cpu = (CPUTrainer) getCurrentPlayer();
            Action action = cpu.decideAction(this);
            executeAction(cpu, action);
            postAction();
        }
    }

    /**
     * Cambia el turno al otro jugador.
     */
    public void changeTurn() {
        this.turn = 3 - this.turn;
    }

    /**
     * Verifica si la batalla ha terminado.
     *
     * @return {@code true} si la batalla ha concluido, {@code false} en caso contrario
     */
    public boolean isFinished() {
        return battleEnded;
    }

    /**
     * Obtiene el entrenador ganador de la batalla.
     *
     * @return El entrenador ganador o {@code null} si la batalla no ha terminado o hay empate
     */
    public Trainer getWinner() {
        if (!battleEnded) return null;

        if (player1.hasAvailablePokemon() && !player2.hasAvailablePokemon()) {
            return player1;
        } else if (player2.hasAvailablePokemon() && !player1.hasAvailablePokemon()) {
            return player2;
        }
        return null;
    }

    /**
     * Establece el clima actual y su duración.
     *
     * @param climate Nombre de la condición climática
     * @param duration Duración en turnos del clima
     */
    public static void setClimate(String climate, int duration) {
        currentClimate = climate;
        climateDuration = duration;
    }

    /**
     * Obtiene el clima actual de la batalla.
     *
     * @return Nombre del clima actual o {@code null} si no hay clima activo
     */
    public static String getClimate() {
        return currentClimate;
    }

    /**
     * Obtiene el entrenador cuyo turno es actualmente.
     *
     * @return Entrenador actual
     */
    public Trainer getCurrentPlayer() {
        return turn == 1 ? player1 : player2;
    }

    /**
     * Obtiene al oponente del jugador actual.
     *
     * @return Entrenador oponente
     */
    public Trainer getOpponent() {
        return turn == 1 ? player2 : player1;
    }

    /**
     * Obtiene el primer entrenador de la batalla.
     *
     * @return Primer entrenador
     */
    public Trainer getPlayer1() {
        return player1;
    }

    /**
     * Obtiene el segundo entrenador de la batalla.
     *
     * @return Segundo entrenador
     */
    public Trainer getPlayer2() {
        return player2;
    }

    /**
     * Obtiene el número del turno actual.
     *
     * @return Número de turno (1 o 2)
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Ejecuta una acción según su tipo: ataque, uso de objeto o cambio de Pokémon.
     *
     * @param current Entrenador que realiza la acción
     * @param action Acción a ejecutar
     */
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

    /**
     * Procesa acciones posteriores a un turno: verificar desmayos y actualizar clima.
     */
    private void postAction() {
        checkFaintedPokemon();
        updateClimate();
    }

    /**
     * Verifica si el Pokémon activo del oponente se ha debilitado y actúa en consecuencia.
     */
    private void checkFaintedPokemon() {
        Trainer opponent = getOpponent();
        Pokemon activeOpponentPokemon = opponent.getActivePokemon();

        if (activeOpponentPokemon != null && activeOpponentPokemon.getHp() <= 0) {
            activeOpponentPokemon.setHp(0);

            if (opponent.getTeam().isAllFainted()) {
                battleEnded = true;
            } else if (opponent.isCPU()) {
                int switchIndex = opponent.getTeam().findHealthyPokemon();
                if (switchIndex != -1) {
                    opponent.switchPokemon(switchIndex);
                }
            }
        }
    }

    /**
     * Actualiza la duración restante del clima activo y lo elimina si expira.
     */
    private void updateClimate() {
        if (climateDuration > 0) {
            climateDuration--;
            if (climateDuration == 0) {
                currentClimate = null;
            }
        }
    }

    /**
     * Procesa los efectos que deben activarse al inicio de cada turno para ambos jugadores.
     * Llama al método correspondiente en el Pokémon activo de cada entrenador si está en combate.
     */
    public void processTurnStartEffects() {
        Trainer[] players = new Trainer[]{player1, player2};
        for (Trainer player : players) {
            Pokemon active = player.getTeam().getActivePokemon();
            if (active != null && active.getHp() > 0) {
                active.processStartOfTurnEffects();
            }
        }
    }

}
