package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa el equipo de un entrenador, conformado por hasta 6 Pokémon.
 */
public class Team implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Pokemon> pokemons;
    private Pokemon activePokemon;
    private int indexActive;

    /**
     * Crea un equipo vacío.
     */
    public Team() {
        pokemons = new ArrayList<>();
        indexActive = 0;
        activePokemon = null;
    }

    /**
     * Agrega un Pokémon al equipo si hay espacio disponible (máximo 6).
     *
     * @param p el Pokémon a agregar
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
     *
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
     *
     * @param index índice del Pokémon al que se desea cambiar
     */
    public void switchPokemon(int index) {
        if (index >= 0 && index < pokemons.size() && pokemons.get(index).getHp() > 0) {
            indexActive = index;
            activePokemon = pokemons.get(index);
        }
    }

    /**
     * Verifica si todos los Pokémon del equipo están debilitados.
     *
     * @return true si todos están con HP 0 o menos
     */
    public boolean isAllFainted() {
        return pokemons.stream().allMatch(p -> p.getHp() <= 0);
    }

    /**
     * Devuelve la lista de Pokémon no debilitados.
     *
     * @return lista de Pokémon con HP > 0
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
     * Encuentra el índice del primer Pokémon saludable que no sea el activo.
     *
     * @return índice o -1 si no hay ninguno
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
     * Verifica si hay al menos un Pokémon saludable que no sea el activo.
     *
     * @return true si hay alguno disponible
     */
    public boolean hasHealthyPokemon() {
        return findHealthyPokemon() != -1;
    }

    /**
     * @return el Pokémon actualmente activo del equipo
     */
    public Pokemon getActivePokemon() {
        return activePokemon;
    }

    /**
     * @return una copia de la lista de todos los Pokémon del equipo
     */
    public List<Pokemon> getPokemons() {
        return new ArrayList<>(pokemons);
    }

    /**
     * @return el índice del Pokémon activo
     */
    public int getActiveIndex() {
        return indexActive;
    }

    /**
     * Cambia automáticamente al siguiente Pokémon disponible si el actual está debilitado.
     *
     * @return true si el cambio fue exitoso, false si todos están debilitados
     */
    public boolean setActivePokemonToNextAvailable() {
        if (activePokemon != null && activePokemon.getHp() > 0) {
            return true;
        }

        for (int i = 0; i < pokemons.size(); i++) {
            int nextIndex = (indexActive + i + 1) % pokemons.size();
            Pokemon nextPokemon = pokemons.get(nextIndex);

            if (nextPokemon.getHp() > 0) {
                switchPokemon(nextIndex);
                return true;
            }
        }

        activePokemon = null;
        return false;
    }

    /**
     * @return número de Pokémon con HP > 0 en el equipo
     */
    public int getHealthyCount() {
        return (int) pokemons.stream()
                .filter(p -> p.getHp() > 0)
                .count();
    }
}
