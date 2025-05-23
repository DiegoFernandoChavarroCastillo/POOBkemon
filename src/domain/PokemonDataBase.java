package domain;

import java.io.Serializable;
import java.util.*;

/**
 * Esta clase representa una base de datos de Pokémon que contiene información sobre
 * diversos Pokémon, incluyendo sus estadísticas y tipos. Proporciona métodos para
 * acceder a los Pokémon de manera individual o aleatoria.
 */
public class PokemonDataBase implements Serializable {
    private static Map<String, Pokemon> pokemons = new HashMap<>();
    private static final long serialVersionUID = 1L;

    static {
        loadInitialPokemons();
    }

    /**
     * Carga los Pokémon iniciales en la base de datos con sus respectivas estadísticas.
     */
    private static void loadInitialPokemons() {
        pokemons.put("Snorlax", new Pokemon(
                "Snorlax", "NORMAL", 524,
                350, 251, 251, 350,
                174, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Porygon2", new Pokemon(
                "Porygon2", "NORMAL", 424,
                251, 262, 262, 273,
                174, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Charizard", new Pokemon(
                "Charizard", "FIRE", 360,
                293, 280, 348, 295,
                328, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Houndoom", new Pokemon(
                "Houndoom", "FIRE", 364,
                273, 211, 319, 251,
                309, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Blastoise", new Pokemon(
                "Blastoise", "WATER", 362,
                291, 328, 295, 339,
                280, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Vaporeon", new Pokemon(
                "Vaporeon", "WATER", 464,
                251, 211, 273, 319,
                251, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Venusaur", new Pokemon(
                "Venusaur", "GRASS", 364,
                289, 291, 328, 328,
                284, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Sceptile", new Pokemon(
                "Sceptile", "GRASS", 344,
                251, 219, 273, 251,
                339, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Raichu", new Pokemon(
                "Raichu", "ELECTRIC", 324,
                306, 229, 306, 284,
                350, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Manectric", new Pokemon(
                "Manectric", "ELECTRIC", 324,
                251, 219, 289, 239,
                319, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Delibird", new Pokemon(
                "Delibird", "ICE", 294,
                229, 207, 251, 207,
                273, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Lapras", new Pokemon(
                "Lapras", "ICE", 464,
                273, 251, 273, 273,
                219, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Machamp", new Pokemon(
                "Machamp", "FIGHTING", 384,
                394, 284, 251, 295,
                229, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Heracross", new Pokemon(
                "Heracross", "FIGHTING", 364,
                339, 229, 229, 249,
                295, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Muk", new Pokemon(
                "Muk", "POISON", 434,
                306, 251, 219, 273,
                174, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Crobat", new Pokemon(
                "Crobat", "POISON", 374,
                295, 229, 229, 249,
                339, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Donphan", new Pokemon(
                "Donphan", "GROUND", 384,
                372, 372, 240, 240,
                218, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Flygon", new Pokemon(
                "Flygon", "GROUND", 344,
                289, 251, 229, 251,
                299, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Aerodactyl", new Pokemon(
                "Aerodactyl", "FLYING", 344,
                309, 229, 229, 229,
                379, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Skarmory", new Pokemon(
                "Skarmory", "FLYING", 334,
                251, 379, 149, 249,
                259, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Gardevoir", new Pokemon(
                "Gardevoir", "PSYCHIC", 340,
                251, 251, 383, 361,
                284, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Alakazam", new Pokemon(
                "Alakazam", "PSYCHIC", 314,
                229, 195, 369, 249,
                339, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Scyther", new Pokemon(
                "Scyther", "BUG", 344,
                309, 229, 229, 229,
                339, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Shuckle", new Pokemon(
                "Shuckle", "BUG", 244,
                95, 479, 95, 479,
                85, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Golem", new Pokemon(
                "Golem", "ROCK", 364,
                295, 339, 195, 229,
                174, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Tyranitar", new Pokemon(
                "Tyranitar", "ROCK", 404,
                403, 350, 317, 328,
                243, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Dusclops", new Pokemon(
                "Dusclops", "GHOST", 284,
                195, 339, 195, 339,
                135, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Gengar", new Pokemon(
                "Gengar", "GHOST", 324,
                251, 240, 394, 273,
                350, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Kingdra", new Pokemon(
                "Kingdra", "DRAGON", 374,
                273, 273, 273, 273,
                251, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Dragonite", new Pokemon(
                "Dragonite", "DRAGON", 386,
                403, 317, 328, 328,
                284, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Umbreon", new Pokemon(
                "Umbreon", "DARK", 394,
                229, 339, 195, 349,
                219, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Absol", new Pokemon(
                "Absol", "DARK", 344,
                339, 209, 229, 229,
                279, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Metagross", new Pokemon(
                "Metagross", "STEEL", 364,
                405, 394, 317, 306,
                262, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Steelix", new Pokemon(
                "Steelix", "STEEL", 354,
                229, 439, 195, 229,
                129, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Wigglytuff", new Pokemon(
                "Wigglytuff", "FAIRY", 484,
                229, 179, 229, 219,
                179, 100, 100,
                new ArrayList<>()
        ));

        pokemons.put("Togetic", new Pokemon(
                "Togetic", "FAIRY", 314,
                196, 295, 284, 339,
                196, 100, 100,
                new ArrayList<>()
        ));
    }

    /**
     * Obtiene una copia del Pokémon con el nombre especificado.
     *
     * @param name El nombre del Pokémon a buscar.
     * @return Una copia del Pokémon solicitado.
     * @throws IllegalArgumentException Si el Pokémon no existe en la base de datos.
     */
    public static Pokemon getPokemon(String name) {
        Pokemon base = pokemons.get(name);
        if (base == null) {
            throw new IllegalArgumentException("No existe el pokémon: " + name);
        }
        return new Pokemon(
                base.getName(), base.getType(), base.getHp(),
                base.getAttack(), base.getDefense(), base.getSpecialAttack(), base.getSpecialDefense(),
                base.getSpeed(), base.getAccuracy(), base.getEvasion(),
                new ArrayList<>(base.getMoves())
        );
    }

    /**
     * Obtiene un conjunto con los nombres de todos los Pokémon disponibles.
     *
     * @return Un conjunto de nombres de Pokémon.
     */
    public static Set<String> getAvailablePokemonNames() {
        return pokemons.keySet();
    }

    /**
     * Obtiene un Pokémon aleatorio de la base de datos.
     *
     * @return Una copia de un Pokémon seleccionado aleatoriamente.
     * @throws IllegalStateException Si no hay Pokémon disponibles en la base de datos.
     */
    public static Pokemon getRandomPokemon() {
        List<String> names = new ArrayList<>(pokemons.keySet());
        if (names.isEmpty()) {
            throw new IllegalStateException("No hay pokémones disponibles.");
        }
        String randomName = names.get(new Random().nextInt(names.size()));
        return getPokemon(randomName);
    }
}