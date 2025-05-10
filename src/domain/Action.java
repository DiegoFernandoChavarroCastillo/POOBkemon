package domain;

/**
 * Clase que representa una acción que puede realizar un jugador durante la batalla.
 * Las acciones pueden ser de tres tipos: atacar, usar ítem o cambiar Pokémon.
 */
public class Action {
    public enum Type {
        ATTACK,      // Representa un ataque con un movimiento
        USE_ITEM,    // Representa el uso de un ítem
        SWITCH_POKEMON // Representa el cambio de Pokémon
    }

    private final Type type;
    private int moveIndex;    // Índice del movimiento (para ataques)
    private int itemIndex;    // Índice del ítem (para usar ítems)
    private int targetIndex;  // Índice del objetivo (para ítems o cambios)

    /**
     * Constructor privado para forzar el uso de los métodos de fábrica
     * @param type Tipo de acción
     */
    private Action(Type type) {
        this.type = type;
    }

    /**
     * Crea una acción de tipo ATAQUE
     * @param moveIndex Índice del movimiento a usar
     * @return Acción de ataque configurada
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
     * Crea una acción de tipo USAR ÍTEM
     * @param itemIndex Índice del ítem a usar
     * @param targetIndex Índice del Pokémon objetivo
     * @return Acción de usar ítem configurada
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
     * Crea una acción de tipo CAMBIAR POKÉMON
     * @param targetIndex Índice del Pokémon al que cambiar
     * @return Acción de cambio configurada
     */
    public static Action createSwitchPokemon(int targetIndex) {
        if (targetIndex < 0) {
            throw new IllegalArgumentException("Índice de Pokémon inválido");
        }

        Action action = new Action(Type.SWITCH_POKEMON);
        action.targetIndex = targetIndex;
        return action;
    }

    // Getters

    public Type getType() {
        return type;
    }

    public int getMoveIndex() {
        if (type != Type.ATTACK) {
            throw new IllegalStateException("Esta acción no es de tipo ATAQUE");
        }
        return moveIndex;
    }

    public int getItemIndex() {
        if (type != Type.USE_ITEM) {
            throw new IllegalStateException("Esta acción no es de tipo USAR_ITEM");
        }
        return itemIndex;
    }

    public int getTargetIndex() {
        if (type == Type.ATTACK) {
            throw new IllegalStateException("Las acciones de ataque no tienen targetIndex");
        }
        return targetIndex;
    }

    @Override
    public String toString() {
        switch (type) {
            case ATTACK:
                return String.format("Acción [ATAQUE, movimiento=%d]", moveIndex);
            case USE_ITEM:
                return String.format("Acción [USAR_ITEM, ítem=%d, objetivo=%d]", itemIndex, targetIndex);
            case SWITCH_POKEMON:
                return String.format("Acción [CAMBIAR_POKEMON, objetivo=%d]", targetIndex);
            default:
                return "Acción [TIPO_DESCONOCIDO]";
        }
    }
}