package domain;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un entrenador de Pokémon, puede ser humano o CPU.
 */
public class Trainer implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String name;
    protected String color;
    protected List<Item> items;
    protected Team team;
    protected boolean isCPU;

    /**
     * Crea un entrenador con un nombre y color asociado.
     *
     * @param name  nombre del entrenador
     * @param color color del entrenador (para interfaz)
     */
    public Trainer(String name, String color) {
        this.name = name;
        this.color = color;
        this.items = new ArrayList<>();
        this.team = new Team();
        this.isCPU = false;
    }

    // Getters básicos

    /** @return nombre del entrenador */
    public String getName() {
        return name;
    }

    /** @return color asociado al entrenador */
    public String getColor() {
        return color;
    }

    /** @return equipo Pokémon del entrenador */
    public Team getTeam() {
        return team;
    }

    /** @return lista de ítems del entrenador */
    public List<Item> getItems() {
        return items;
    }

    /** @return true si el entrenador es controlado por CPU */
    public boolean isCPU() {
        return isCPU;
    }

    // Métodos de gestión de ítems

    /**
     * Agrega un ítem al inventario del entrenador (máx. 6 ítems).
     *
     * @param item ítem a agregar
     */
    public void addItem(Item item) {
        if (items.size() < 6) {
            items.add(item);
        }
    }

    // Métodos de batalla

    /**
     * Ejecuta un ataque del Pokémon activo contra el Pokémon activo del oponente.
     *
     * @param moveIndex índice del movimiento a usar
     * @param opponent  entrenador oponente
     */
    public void attack(int moveIndex, Trainer opponent) {
        Pokemon myPokemon = team.getActivePokemon();
        Pokemon opponentPokemon = opponent.getTeam().getActivePokemon();
        if (myPokemon != null && myPokemon.getHp() > 0 &&
                opponentPokemon != null && opponentPokemon.getHp() > 0) {
            myPokemon.attack(moveIndex, opponentPokemon);
        }
    }

    /**
     * Usa un ítem sobre un Pokémon del equipo.
     *
     * @param itemIndex   índice del ítem en la lista
     * @param targetIndex índice del Pokémon objetivo
     */
    public void useItem(int itemIndex, int targetIndex) {
        if (itemIndex < 0 || itemIndex >= items.size()) return;

        Item item = items.get(itemIndex);
        Pokemon target = team.getPokemons().get(targetIndex);

        if (item instanceof Revive) {
            if (target.getHp() > 0) {
                JOptionPane.showMessageDialog(null,
                        "¡No puedes usar Revive en un Pokémon que no está debilitado!");
                return;
            }
        } else {
            if (target.getHp() <= 0) {
                JOptionPane.showMessageDialog(null,
                        "¡No puedes usar pociones en un Pokémon debilitado!");
                return;
            }
        }

        item.use(target);
        items.remove(itemIndex);
    }

    /**
     * Establece como activo al Pokémon en el índice indicado.
     *
     * @param index índice del Pokémon
     */
    public void setActivePokemon(int index) {
        team.setActivePokemon(index);
    }

    /**
     * Cambia el Pokémon activo al del índice indicado.
     *
     * @param index índice del nuevo Pokémon activo
     */
    public void switchPokemon(int index) {
        team.switchPokemon(index);
    }

    /**
     * Verifica si el entrenador tiene al menos un Pokémon disponible.
     *
     * @return true si hay Pokémon con vida
     */
    public boolean hasAvailablePokemon() {
        return !team.isAllFainted();
    }

    /**
     * @return el Pokémon actualmente activo del equipo
     */
    public Pokemon getActivePokemon() {
        return team.getActivePokemon();
    }

    /**
     * Agrega un Pokémon al equipo del entrenador.
     *
     * @param p el Pokémon a agregar
     */
    public void addPokemonToTeam(Pokemon p) {
        team.addPokemon(p);
    }
}
