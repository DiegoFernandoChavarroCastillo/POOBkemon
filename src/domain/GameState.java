package domain;


import java.io.Serializable;
import java.util.List;

/**
 * Clase que representa el estado completo del juego para serialización
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Battle battle;
    private final int gameMode;
    private final String player1Name;
    private final String player2Name;

    public GameState(Battle battle, int gameMode, String player1Name, String player2Name) {
        this.battle = battle;
        this.gameMode = gameMode;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
    }

    // Getters
    public Battle getBattle() { return battle; }
    public int getGameMode() { return gameMode; }
    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }
}