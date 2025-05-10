package domain;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un entrenador de Pokémon, puede ser humano o CPU.
 */
public class Trainer {
    protected String name;
    protected String color;
    protected List<Item> items;
    protected Team team;
    protected boolean isCPU;

    public Trainer(String name, String color) {
        this.name = name;
        this.color = color;
        this.items = new ArrayList<>();
        this.team = new Team();
        this.isCPU = false;
    }

    // Getters básicos
    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Team getTeam() {
        return team;
    }

    public List<Item> getItems() {
        return items;
    }

    public boolean isCPU() {
        return isCPU;
    }

    // Métodos de gestión de items
    public void addItem(Item item) {
        if (items.size() < 6) {
            items.add(item);
        }
    }

    // Métodos de batalla
    public void attack(int moveIndex, Trainer opponent) {
        Pokemon myPokemon = team.getActivePokemon();
        Pokemon opponentPokemon = opponent.getTeam().getActivePokemon();
        if (myPokemon != null && myPokemon.getHp() > 0 &&
                opponentPokemon != null && opponentPokemon.getHp() > 0) {
            myPokemon.attack(moveIndex, opponentPokemon);
        }
    }

    public void useItem(int itemIndex, int targetIndex) {
        if (itemIndex < 0 || itemIndex >= items.size()) return;

        Item item = items.get(itemIndex);
        Pokemon target = team.getPokemons().get(targetIndex);

        // Validar uso correcto del Revive
        if (item instanceof Revive) {
            if (target.getHp() > 0) {
                JOptionPane.showMessageDialog(null,
                        "¡No puedes usar Revive en un Pokémon que no está debilitado!");
                return;
            }
        } else { // Pociones
            if (target.getHp() <= 0) {
                JOptionPane.showMessageDialog(null,
                        "¡No puedes usar pociones en un Pokémon debilitado!");
                return;
            }
        }

        item.use(target);
        items.remove(itemIndex); // Consumir el ítem
    }

    public void setActivePokemon(int index) {
        team.setActivePokemon(index); // ✅ Delegado al equipo
    }

    public void switchPokemon(int index) {
        team.switchPokemon(index);
    }

    public boolean hasAvailablePokemon() {
        return !team.isAllFainted();
    }

    public Pokemon getActivePokemon() {
        return team.getActivePokemon();
    }

    public void addPokemonToTeam(Pokemon p) {
        team.addPokemon(p);
    }
}
