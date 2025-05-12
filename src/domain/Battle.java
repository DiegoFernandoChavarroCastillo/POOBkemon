package domain;

/**
 * Class that handles the logic of a Pokémon battle between two trainers.
 * Manages turn flow, action execution, status checks, and weather conditions during battle.
 */
public class Battle {
    private Trainer player1;
    private Trainer player2;
    private int turn;
    private boolean battleEnded;
    private static String currentClimate = null;
    private static int climateDuration = 0;

    /**
     * Constructs a new battle between two trainers.
     *
     * @param player1 First trainer in the battle
     * @param player2 Second trainer in the battle
     */
    public Battle(Trainer player1, Trainer player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.turn = 1;
        this.battleEnded = false;
    }

    /**
     * Gets the current battle state.
     *
     * @return BattleState object containing current battle information
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
     * Executes an action performed by the current player.
     *
     * @param action Action to be performed
     * @throws IllegalStateException if battle has ended or current player is CPU
     */
    public void performAction(Action action) {
        if (battleEnded) {
            throw new IllegalStateException("Battle has ended");
        }

        Trainer current = getCurrentPlayer();
        if (current.isCPU()) {
            throw new IllegalStateException("Cannot perform manual actions for CPU");
        }

        executeAction(current, action);
        postAction();
    }

    /**
     * Executes the automatic turn when current player is CPU.
     */
    public void executeCpuTurn() {
        if (!battleEnded && getCurrentPlayer().isCPU()) {
            CPUTrainer cpu = (CPUTrainer) getCurrentPlayer();
            Action action = cpu.decideAction(this);
            executeAction(cpu, action);
            postAction();
        }
    }

    /**
     * Switches turn to the other player.
     */
    public void changeTurn() {
        this.turn = 3 - this.turn;
    }

    /**
     * Checks if the battle has ended.
     *
     * @return true if battle has ended, false otherwise
     */
    public boolean isFinished() {
        return battleEnded;
    }

    /**
     * Gets the winning trainer of the battle.
     *
     * @return Winning trainer or null if battle hasn't ended or is a draw
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
     * Sets the current weather and its duration.
     *
     * @param climate Name of weather condition to set
     * @param duration Duration in turns for the weather
     */
    public static void setClimate(String climate, int duration) {
        currentClimate = climate;
        climateDuration = duration;
    }

    /**
     * Gets the current battle weather.
     *
     * @return Current weather name or null if no active weather
     */
    public static String getClimate() {
        return currentClimate;
    }

    /**
     * Gets the trainer whose turn it currently is.
     *
     * @return Current trainer
     */
    public Trainer getCurrentPlayer() {
        return turn == 1 ? player1 : player2;
    }

    /**
     * Gets the opponent of the current player.
     *
     * @return Opponent trainer
     */
    public Trainer getOpponent() {
        return turn == 1 ? player2 : player1;
    }

    /**
     * Gets the first trainer in the battle.
     *
     * @return First trainer
     */
    public Trainer getPlayer1() {
        return player1;
    }

    /**
     * Gets the second trainer in the battle.
     *
     * @return Second trainer
     */
    public Trainer getPlayer2() {
        return player2;
    }

    /**
     * Gets the current turn number.
     *
     * @return Current turn number (1 or 2)
     */
    public int getTurn() {
        return turn;
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
    }

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

    private void updateClimate() {
        if (climateDuration > 0) {
            climateDuration--;
            if (climateDuration == 0) {
                currentClimate = null;
            }
        }
    }
}