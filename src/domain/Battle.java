package domain;



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

    public Battle(Trainer player1, Trainer player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.turn = 1;
        this.battleEnded = false;
    }

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

    public void executeCpuTurn() {
        if (!battleEnded && getCurrentPlayer().isCPU()) {
            CPUTrainer cpu = (CPUTrainer) getCurrentPlayer();
            Action action = cpu.decideAction();
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
        return null;
    }

    public static void setClimate(String climate, int duration) {
        currentClimate = climate;
        climateDuration = duration;
    }

    public static String getClimate() {
        return currentClimate;
    }

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
}

