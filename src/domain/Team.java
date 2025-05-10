package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa el equipo de un entrenador, conformado por hasta 6 Pokémon.
 */
public class Team {
    private List<Pokemon> pokemons;
    private Pokemon activePokemon;
    private int indexActive;

    public Team() {
        pokemons = new ArrayList<>();
        indexActive = 0;
        activePokemon = null;
    }

    /**
     * Agrega un Pokémon al equipo.
     */
    public void addPokemon(Pokemon p) {
        if (pokemons.size() < 6) {
            pokemons.add(p);
            if (activePokemon == null) {
                activePokemon = p;
                indexActive = pokemons.size() - 1;
            }
        }
    }

    /**
     * Establece como Pokémon activo el de la posición indicada.
     * @param index índice del Pokémon a establecer como activo
     */
    public void setActivePokemon(int index) {
        if (index >= 0 && index < pokemons.size() && pokemons.get(index).getHp() > 0) {
            indexActive = index;
            activePokemon = pokemons.get(index);
        }
    }

    /**
     * Cambia el Pokémon activo por el de la posición indicada.
     */
    public void switchPokemon(int index) {
        if (index >= 0 && index < pokemons.size() && pokemons.get(index).getHp() > 0) {
            indexActive = index;
            activePokemon = pokemons.get(index);
        }
    }

    /**
     * Verifica si todos los Pokémon están debilitados.
     */
    public boolean isAllFainted() {
        return pokemons.stream().allMatch(p -> p.getHp() <= 0);
    }

    /**
     * Devuelve la lista de Pokémon disponibles (no debilitados).
     */
    public List<Pokemon> getAvailablePokemons() {
        List<Pokemon> available = new ArrayList<>();
        for (Pokemon p : pokemons) {
            if (p.getHp() > 0) {
                available.add(p);
            }
        }
        return available;
    }

    /**
     * Encuentra el índice del primer Pokémon saludable en el equipo (excluyendo al activo)
     * @return índice del Pokémon saludable, o -1 si no hay ninguno
     */
    public int findHealthyPokemon() {
        for (int i = 0; i < pokemons.size(); i++) {
            Pokemon p = pokemons.get(i);
            if (p.getHp() > 0 && i != indexActive) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Verifica si hay Pokémon saludables disponibles para cambiar
     */
    public boolean hasHealthyPokemon() {
        return findHealthyPokemon() != -1;
    }

    public Pokemon getActivePokemon() {
        return activePokemon;
    }

    public List<Pokemon> getPokemons() {
        return new ArrayList<>(pokemons); // Devuelve una copia para evitar modificaciones externas
    }

    public int getActiveIndex() {
        return indexActive;
    }

    /**
     * Cambia automáticamente al siguiente Pokémon disponible cuando el activo se debilita.
     * @return true si encontró un Pokémon disponible, false si todos están debilitados
     */
    public boolean setActivePokemonToNextAvailable() {
        // Si el Pokémon actual aún tiene vida, no hacer nada
        if (activePokemon != null && activePokemon.getHp() > 0) {
            return true;
        }

        // Buscar el siguiente Pokémon disponible
        for (int i = 0; i < pokemons.size(); i++) {
            int nextIndex = (indexActive + i + 1) % pokemons.size();
            Pokemon nextPokemon = pokemons.get(nextIndex);

            if (nextPokemon.getHp() > 0) {
                switchPokemon(nextIndex);
                return true;
            }
        }

        // Si no hay Pokémon disponibles
        activePokemon = null;
        return false;
    }
    public int getHealthyCount() {
        return (int) pokemons.stream()
                .filter(p -> p.getHp() > 0)
                .count();
    }
}