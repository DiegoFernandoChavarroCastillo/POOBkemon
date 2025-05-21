package domain;

import java.io.Serializable;

/**
 * Clase que representa el estado actual de la batalla para la interfaz de usuario.
 * Contiene información relevante como los nombres de los entrenadores, sus Pokémon activos,
 * el turno actual, si el jugador activo es humano y el clima en curso.
 */
public class BattleState implements Serializable {
    private final String player1Name;
    private final String player2Name;
    private final Pokemon player1Pokemon;
    private final Pokemon player2Pokemon;
    private final boolean isPlayer1Turn;
    private final boolean isHumanTurn;
    private final String climate;
    private static final long serialVersionUID = 1L;

    /**
     * Crea un nuevo estado de batalla con los datos actuales.
     *
     * @param player1Name Nombre del primer jugador
     * @param player2Name Nombre del segundo jugador
     * @param player1Pokemon Pokémon activo del primer jugador
     * @param player2Pokemon Pokémon activo del segundo jugador
     * @param isPlayer1Turn Indica si es el turno del primer jugador
     * @param isHumanTurn Indica si el turno actual pertenece a un jugador humano
     * @param climate Clima actual de la batalla (puede ser {@code null})
     */
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

    /**
     * Obtiene el nombre del primer jugador.
     *
     * @return Nombre del jugador 1
     */
    public String getPlayer1Name() {
        return player1Name;
    }

    /**
     * Obtiene el nombre del segundo jugador.
     *
     * @return Nombre del jugador 2
     */
    public String getPlayer2Name() {
        return player2Name;
    }

    /**
     * Obtiene el Pokémon activo del primer jugador.
     *
     * @return Pokémon del jugador 1
     */
    public Pokemon getPlayer1Pokemon() {
        return player1Pokemon;
    }

    /**
     * Obtiene el Pokémon activo del segundo jugador.
     *
     * @return Pokémon del jugador 2
     */
    public Pokemon getPlayer2Pokemon() {
        return player2Pokemon;
    }

    /**
     * Indica si es el turno del primer jugador.
     *
     * @return {@code true} si es el turno del jugador 1, {@code false} si es del jugador 2
     */
    public boolean isPlayer1Turn() {
        return isPlayer1Turn;
    }

    /**
     * Indica si el turno actual pertenece a un jugador humano.
     *
     * @return {@code true} si el jugador activo es humano, {@code false} si es una CPU
     */
    public boolean isHumanTurn() {
        return isHumanTurn;
    }

    /**
     * Obtiene el clima actual en la batalla.
     *
     * @return Nombre del clima o {@code null} si no hay clima activo
     */
    public String getClimate() {
        return climate;
    }

    /**
     * Obtiene el nombre del jugador que tiene el turno actual.
     *
     * @return Nombre del jugador activo
     */
    public String getCurrentPlayerName() {
        return isPlayer1Turn ? player1Name : player2Name;
    }
}
