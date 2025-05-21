package domain;

/**
 * Interfaz para notificar eventos que ocurren durante la batalla.
 * Permite que diferentes componentes reaccionen a los eventos sin acoplamiento directo.
 */
public interface BattleEventListener {
    /**
     * Notifica cuando se realiza un ataque durante la batalla.
     *
     * @param attackerName Nombre del Pokémon atacante
     * @param targetName Nombre del Pokémon objetivo
     * @param moveName Nombre del movimiento utilizado
     */
    void onAttackPerformed(String attackerName, String targetName, String moveName);

    /**
     * Notifica cuando se usa un ítem durante la batalla.
     *
     * @param playerName Nombre del jugador que usa el ítem
     * @param itemName Nombre del ítem utilizado
     * @param targetName Nombre del Pokémon objetivo
     */
    void onItemUsed(String playerName, String itemName, String targetName);

    /**
     * Notifica cuando se cambia de Pokémon durante la batalla.
     *
     * @param playerName Nombre del jugador que realiza el cambio
     * @param pokemonName Nombre del nuevo Pokémon activo
     */
    void onPokemonSwitched(String playerName, String pokemonName);

    /**
     * Notifica cuando un Pokémon recibe daño.
     *
     * @param pokemonName Nombre del Pokémon afectado
     * @param damage Cantidad de daño recibido
     */
    void onDamageReceived(String pokemonName, int damage);

    /**
     * Notifica cuando un Pokémon es debilitado.
     *
     * @param pokemonName Nombre del Pokémon debilitado
     */
    void onPokemonFainted(String pokemonName);
}