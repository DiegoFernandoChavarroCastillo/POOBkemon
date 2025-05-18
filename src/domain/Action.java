package domain;

import java.io.Serializable;

/**
 * Representa una acción que un jugador puede realizar durante una batalla.
 * Una acción puede ser de tipo ATAQUE, USAR_OBJETO o CAMBIAR_POKEMON.
 * Esta clase utiliza métodos de fábrica estáticos para asegurar la creación válida de acciones.
 */
public class Action implements Serializable {

    /**
     * Enumera los tipos de acciones que se pueden realizar en batalla.
     */
    public enum Type {
        /** Acción para realizar un ataque usando un movimiento. */
        ATTACK,
        /** Acción para usar un objeto en un Pokémon. */
        USE_ITEM,
        /** Acción para cambiar el Pokémon activo. */
        SWITCH_POKEMON
    }

    private final Type type;
    private int moveIndex;
    private int itemIndex;
    private int targetIndex;
    private static final long serialVersionUID = 1L;

    /**
     * Constructor privado para forzar el uso de métodos de fábrica.
     *
     * @param type el tipo de acción
     */
    private Action(Type type) {
        this.type = type;
    }

    /**
     * Crea una acción de tipo ATAQUE.
     *
     * @param moveIndex el índice del movimiento a usar (-1 representa Forcejeo)
     * @return una instancia de {@code Action} de tipo {@code ATTACK}
     * @throws IllegalArgumentException si {@code moveIndex} es menor que -1
     */
    public static Action createAttack(int moveIndex) {
        if (moveIndex < -1) {
            throw new IllegalArgumentException("Índice de movimiento inválido");
        }
        Action action = new Action(Type.ATTACK);
        action.moveIndex = moveIndex;
        return action;
    }

    /**
     * Crea una acción para usar un objeto sobre un Pokémon.
     *
     * @param itemIndex el índice del objeto a usar
     * @param targetIndex el índice del Pokémon objetivo
     * @return una instancia de {@code Action} de tipo {@code USE_ITEM}
     * @throws IllegalArgumentException si {@code itemIndex} o {@code targetIndex} son negativos
     */
    public static Action createUseItem(int itemIndex, int targetIndex) {
        if (itemIndex < 0 || targetIndex < 0) {
            throw new IllegalArgumentException("Índices inválidos");
        }
        Action action = new Action(Type.USE_ITEM);
        action.itemIndex = itemIndex;
        action.targetIndex = targetIndex;
        return action;
    }

    /**
     * Crea una acción para cambiar de Pokémon.
     *
     * @param targetIndex el índice del Pokémon al que se desea cambiar
     * @return una instancia de {@code Action} de tipo {@code SWITCH_POKEMON}
     * @throws IllegalArgumentException si {@code targetIndex} es negativo
     */
    public static Action createSwitchPokemon(int targetIndex) {
        if (targetIndex < 0) {
            throw new IllegalArgumentException("Índice de Pokémon inválido");
        }
        Action action = new Action(Type.SWITCH_POKEMON);
        action.targetIndex = targetIndex;
        return action;
    }

    /**
     * Devuelve el tipo de esta acción.
     *
     * @return el tipo de acción
     */
    public Type getType() {
        return type;
    }

    /**
     * Devuelve el índice del movimiento en acciones de tipo ATAQUE.
     *
     * @return el índice del movimiento
     * @throws IllegalStateException si la acción no es de tipo {@code ATTACK}
     */
    public int getMoveIndex() {
        if (type != Type.ATTACK) {
            throw new IllegalStateException("Esta acción no es de tipo ATTACK");
        }
        return moveIndex;
    }

    /**
     * Devuelve el índice del objeto en acciones de tipo USAR_OBJETO.
     *
     * @return el índice del objeto
     * @throws IllegalStateException si la acción no es de tipo {@code USE_ITEM}
     */
    public int getItemIndex() {
        if (type != Type.USE_ITEM) {
            throw new IllegalStateException("Esta acción no es de tipo USE_ITEM");
        }
        return itemIndex;
    }

    /**
     * Devuelve el índice del objetivo en acciones de tipo USAR_OBJETO o CAMBIAR_POKEMON.
     *
     * @return el índice del objetivo
     * @throws IllegalStateException si la acción es de tipo {@code ATTACK}
     */
    public int getTargetIndex() {
        if (type == Type.ATTACK) {
            throw new IllegalStateException("Las acciones de tipo ATTACK no tienen índice de objetivo");
        }
        return targetIndex;
    }

    /**
     * Devuelve una representación en forma de cadena de esta acción.
     *
     * @return una cadena describiendo la acción
     */
    @Override
    public String toString() {
        switch (type) {
            case ATTACK:
                return String.format("Acción [ATTACK, movimiento=%d]", moveIndex);
            case USE_ITEM:
                return String.format("Acción [USE_ITEM, objeto=%d, objetivo=%d]", itemIndex, targetIndex);
            case SWITCH_POKEMON:
                return String.format("Acción [SWITCH_POKEMON, objetivo=%d]", targetIndex);
            default:
                return String.format("Acción [TIPO_DESCONOCIDO: %s]", type);
        }
    }
}
