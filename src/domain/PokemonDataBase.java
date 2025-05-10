package domain;

import java.util.*;

public class PokemonDataBase {
    private static Map<String, Pokemon> pokemons = new HashMap<>();

    static {
        loadInitialPokemons();
    }

    private static void loadInitialPokemons() {
        // Movimientos de Charizard (ArrayList vacío)
        pokemons.put("Charizard", new Pokemon(
                "Charizard", "FIRE", 360,
                293, 280, 348, 295,
                328, 100, 100,
                new ArrayList<>()  // Movimientos vacíos
        ));

        // Movimientos de Blastoise (ArrayList vacío)
        pokemons.put("Blastoise", new Pokemon(
                "Blastoise", "WATER", 362,
                291, 328, 295, 339,
                280, 100, 100,
                new ArrayList<>()  // Movimientos vacíos
        ));

        // Movimientos de Venusaur (ArrayList vacío)
        pokemons.put("Venusaur", new Pokemon(
                "Venusaur", "GRASS", 364,
                289, 291, 328, 328,
                284, 100, 100,
                new ArrayList<>()  // Movimientos vacíos
        ));

        // Movimientos de Gengar (ArrayList vacío)
        pokemons.put("Gengar", new Pokemon(
                "Gengar", "GHOST", 324,
                251, 240, 394, 273,
                350, 100, 100,
                new ArrayList<>()  // Movimientos vacíos
        ));
    }

    public static Pokemon getPokemon(String name) {
        Pokemon base = pokemons.get(name);
        if (base == null) {
            throw new IllegalArgumentException("No existe el pokémon: " + name);
        }
        return new Pokemon(
                base.getName(), base.getType(), base.getHp(),
                base.getAttack(), base.getDefense(), base.getSpecialAttack(), base.getSpecialDefense(),
                base.getSpeed(), base.getAccuracy(), base.getEvasion(),
                new ArrayList<>(base.getMoves())  // Copia del ArrayList vacío
        );
    }

    public static Set<String> getAvailablePokemonNames() {
        return pokemons.keySet();
    }
}