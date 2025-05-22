package domain;

import java.io.Serializable;
/**
 * Enumeración que representa los posibles objetivos de una acción en el juego,
 * como ataques, movimientos o efectos. Implementa {@link Serializable} para permitir
 * la serialización de objetos que contengan este tipo.
 *
 * <p>Esta enumeración es utilizada para determinar si una acción afecta al Pokémon
 * del usuario o al Pokémon del oponente.</p>
 */
public enum Target implements Serializable {
    USER, OPPONENT
}
