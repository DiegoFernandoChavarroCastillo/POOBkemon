package domain;

/**
 * Class representing an action a player can perform during battle.
 * Actions can be of three types: attack, use item, or switch Pokémon.
 */
public class Action {
    /**
     * Enum representing the different types of actions available.
     */
    public enum Type {
        /** Represents an attack using a move */
        ATTACK,
        /** Represents using an item */
        USE_ITEM,
        /** Represents switching Pokémon */
        SWITCH_POKEMON
    }

    private final Type type;
    private int moveIndex;
    private int itemIndex;
    private int targetIndex;

    /**
     * Private constructor to enforce factory method usage.
     * @param type The type of action
     */
    private Action(Type type) {
        this.type = type;
    }

    /**
     * Creates an ATTACK type action.
     * @param moveIndex Index of the move to use (-1 represents Struggle)
     * @return Configured attack action
     * @throws IllegalArgumentException if moveIndex is invalid
     */
    public static Action createAttack(int moveIndex) {
        if (moveIndex < -1) {
            throw new IllegalArgumentException("Invalid move index");
        }

        Action action = new Action(Type.ATTACK);
        action.moveIndex = moveIndex;
        return action;
    }

    /**
     * Creates a USE ITEM type action.
     * @param itemIndex Index of the item to use
     * @param targetIndex Index of the target Pokémon
     * @return Configured use item action
     * @throws IllegalArgumentException if indices are invalid
     */
    public static Action createUseItem(int itemIndex, int targetIndex) {
        if (itemIndex < 0 || targetIndex < 0) {
            throw new IllegalArgumentException("Invalid indices");
        }

        Action action = new Action(Type.USE_ITEM);
        action.itemIndex = itemIndex;
        action.targetIndex = targetIndex;
        return action;
    }

    /**
     * Creates a SWITCH POKÉMON type action.
     * @param targetIndex Index of the Pokémon to switch to
     * @return Configured switch action
     * @throws IllegalArgumentException if targetIndex is invalid
     */
    public static Action createSwitchPokemon(int targetIndex) {
        if (targetIndex < 0) {
            throw new IllegalArgumentException("Invalid Pokémon index");
        }

        Action action = new Action(Type.SWITCH_POKEMON);
        action.targetIndex = targetIndex;
        return action;
    }

    /**
     * Gets the type of this action.
     * @return The action type
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets the move index for ATTACK actions.
     * @return The move index
     * @throws IllegalStateException if action is not ATTACK type
     */
    public int getMoveIndex() {
        if (type != Type.ATTACK) {
            throw new IllegalStateException("This action is not ATTACK type");
        }
        return moveIndex;
    }

    /**
     * Gets the item index for USE ITEM actions.
     * @return The item index
     * @throws IllegalStateException if action is not USE ITEM type
     */
    public int getItemIndex() {
        if (type != Type.USE_ITEM) {
            throw new IllegalStateException("This action is not USE_ITEM type");
        }
        return itemIndex;
    }

    /**
     * Gets the target index for USE ITEM or SWITCH POKÉMON actions.
     * @return The target index
     * @throws IllegalStateException if action is ATTACK type
     */
    public int getTargetIndex() {
        if (type == Type.ATTACK) {
            throw new IllegalStateException("ATTACK actions don't have targetIndex");
        }
        return targetIndex;
    }

    /**
     * Returns a string representation of the action.
     * @return String describing the action
     */
    @Override
    public String toString() {
        switch (type) {
            case ATTACK:
                return String.format("Action [ATTACK, move=%d]", moveIndex);
            case USE_ITEM:
                return String.format("Action [USE_ITEM, item=%d, target=%d]", itemIndex, targetIndex);
            case SWITCH_POKEMON:
                return String.format("Action [SWITCH_POKEMON, target=%d]", targetIndex);
            default:
                return "Action [UNKNOWN_TYPE]";
        }
    }
}