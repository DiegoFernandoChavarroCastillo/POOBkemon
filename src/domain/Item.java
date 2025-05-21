package domain;

import java.io.Serializable;

/**
 * Clase abstracta que representa un ítem utilizable en batalla.
 * Los ítems pueden tener efectos de curación y/o revivir Pokémon.
 */
public abstract class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Nombre del ítem.
     */
    protected String name;

    /**
     * Indica si el ítem tiene la capacidad de revivir a un Pokémon.
     */
    protected boolean REVIVES;

    /**
     * Cantidad de puntos de salud (HP) que el ítem puede restaurar.
     */
    protected int HEAL;

    /**
     * Crea un nuevo ítem con sus propiedades básicas.
     *
     * @param name    nombre del ítem
     * @param revives indica si el ítem puede revivir
     * @param heal    cantidad de HP que puede restaurar
     */
    public Item(String name, boolean revives, int heal) {
        this.name = name;
        this.REVIVES = revives;
        this.HEAL = heal;
    }

    /**
     * Aplica el efecto del ítem sobre el Pokémon especificado.
     *
     * @param pokemon el Pokémon objetivo del ítem
     */
    public abstract void use(Pokemon pokemon);

    /**
     * Retorna el nombre del ítem.
     *
     * @return nombre del ítem
     */
    public String getName() {
        return name;
    }
}
