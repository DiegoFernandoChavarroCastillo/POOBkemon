package domain;

import java.io.Serializable;

/**
 * Representa el estado completo del juego, incluyendo la batalla,
 * el modo de juego y los nombres de los jugadores.
 * Esta clase es serializable para permitir guardar y cargar partidas.
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Battle battle;
    private final int gameMode;
    private final String player1Name;
    private final String player2Name;

    /**
     * Crea una nueva instancia de GameState con la información completa del juego.
     *
     * @param battle       la batalla en curso
     * @param gameMode     el modo de juego actual
     * @param player1Name  nombre del primer jugador
     * @param player2Name  nombre del segundo jugador
     */
    public GameState(Battle battle, int gameMode, String player1Name, String player2Name) {
        this.battle = battle;
        this.gameMode = gameMode;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
    }

    /**
     * Retorna la batalla actual.
     *
     * @return la instancia de Battle
     */
    public Battle getBattle() {
        return battle;
    }

    /**
     * Retorna el modo de juego actual.
     *
     * @return el modo de juego como entero
     */
    public int getGameMode() {
        return gameMode;
    }

    /**
     * Retorna el nombre del primer jugador.
     *
     * @return nombre del jugador 1
     */
    public String getPlayer1Name() {
        return player1Name;
    }

    /**
     * Retorna el nombre del segundo jugador.
     *
     * @return nombre del jugador 2
     */
    public String getPlayer2Name() {
        return player2Name;
    }
}
