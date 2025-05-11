package domain;

/**
 * Clase que representa el estado actual de la batalla para la UI
 */
public class BattleState {
    private final String player1Name;
    private final String player2Name;
    private final Pokemon player1Pokemon;
    private final Pokemon player2Pokemon;
    private final boolean isPlayer1Turn;
    private final boolean isHumanTurn;
    private final String climate;

    public BattleState(String player1Name, String player2Name,
                       Pokemon player1Pokemon, Pokemon player2Pokemon,
                       boolean isPlayer1Turn, boolean isHumanTurn,
                       String climate) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.player1Pokemon = player1Pokemon;
        this.player2Pokemon = player2Pokemon;
        this.isPlayer1Turn = isPlayer1Turn;
        this.isHumanTurn = isHumanTurn;
        this.climate = climate;
    }

    // Getters
    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }
    public Pokemon getPlayer1Pokemon() { return player1Pokemon; }
    public Pokemon getPlayer2Pokemon() { return player2Pokemon; }
    public boolean isPlayer1Turn() { return isPlayer1Turn; }
    public boolean isHumanTurn() { return isHumanTurn; }
    public String getClimate() { return climate; }
    public String getCurrentPlayerName() { return isPlayer1Turn ? player1Name : player2Name; }
}
