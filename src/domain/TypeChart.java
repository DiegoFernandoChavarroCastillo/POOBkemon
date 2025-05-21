package domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa la tabla de efectividades entre tipos de Pokémon.
 * Permite consultar multiplicadores de daño según el tipo de ataque y el tipo del objetivo.
 */
public class TypeChart implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Map<String, Map<String, Double>> chart = new HashMap<>();

    static {
        // Definiciones de efectividad por tipo
        add("ACERO", "HADA", 2.0);
        add("ACERO", "HIELO", 2.0);
        add("ACERO", "ROCA", 2.0);
        add("ACERO", "ACERO", 0.5);
        add("ACERO", "AGUA", 0.5);
        add("ACERO", "ELECTR", 0.5);
        add("ACERO", "FUEGO", 0.5);
        add("ACERO", "FANT", 1.0);
        add("ACERO", "LUCHA", 1.0);

        add("AGUA", "FUEGO", 2.0);
        add("AGUA", "ROCA", 2.0);
        add("AGUA", "TIERRA", 2.0);
        add("AGUA", "AGUA", 0.5);
        add("AGUA", "DRAGON", 0.5);
        add("AGUA", "PLANTA", 0.5);

        add("BICHO", "PLANTA", 2.0);
        add("BICHO", "PSIQUICO", 2.0);
        add("BICHO", "SINI", 2.0);
        add("BICHO", "FUEGO", 0.5);
        add("BICHO", "HADA", 0.5);
        add("BICHO", "LUCHA", 0.5);
        add("BICHO", "VENENO", 0.5);
        add("BICHO", "ACERO", 0.5);
        add("BICHO", "FANT", 0.5);

        add("DRAGON", "DRAGON", 2.0);
        add("DRAGON", "ACERO", 0.5);
        add("DRAGON", "HADA", 0.0);

        add("ELECTR", "AGUA", 2.0);
        add("ELECTR", "VOLADOR", 2.0);
        add("ELECTR", "DRAGON", 0.5);
        add("ELECTR", "ELECTR", 0.5);
        add("ELECTR", "PLANTA", 0.5);
        add("ELECTR", "TIERRA", 0.0);

        add("FANT", "FANT", 2.0);
        add("FANT", "PSIQUICO", 2.0);
        add("FANT", "NORMAL", 0.0);
        add("FANT", "SINIESTRO", 0.5);

        add("FUEGO", "PLANTA", 2.0);
        add("FUEGO", "HIELO", 2.0);
        add("FUEGO", "BICHO", 2.0);
        add("FUEGO", "ACERO", 2.0);
        add("FUEGO", "AGUA", 0.5);
        add("FUEGO", "DRAGON", 0.5);
        add("FUEGO", "FUEGO", 0.5);
        add("FUEGO", "ROCA", 0.5);

        add("HADA", "DRAGON", 2.0);
        add("HADA", "LUCHA", 2.0);
        add("HADA", "SINI", 2.0);
        add("HADA", "FUEGO", 0.5);
        add("HADA", "VENENO", 0.5);
        add("HADA", "ACERO", 0.5);

        add("HIELO", "DRAGON", 2.0);
        add("HIELO", "PLANTA", 2.0);
        add("HIELO", "TIERRA", 2.0);
        add("HIELO", "VOLADOR", 2.0);
        add("HIELO", "AGUA", 0.5);
        add("HIELO", "FUEGO", 0.5);
        add("HIELO", "HIELO", 0.5);
        add("HIELO", "ACERO", 0.5);

        add("LUCHA", "ACERO", 2.0);
        add("LUCHA", "HIELO", 2.0);
        add("LUCHA", "NORMAL", 2.0);
        add("LUCHA", "ROCA", 2.0);
        add("LUCHA", "SINIESTRO", 2.0);
        add("LUCHA", "BICHO", 0.5);
        add("LUCHA", "HADA", 0.5);
        add("LUCHA", "VENENO", 0.5);
        add("LUCHA", "VOLADOR", 0.5);
        add("LUCHA", "PSIQUICO", 0.5);
        add("LUCHA", "FANT", 0.0);
    }

    /**
     * Agrega una relación de efectividad entre tipos a la tabla.
     *
     * @param attackType tipo del movimiento atacante
     * @param targetType tipo del defensor
     * @param multiplier multiplicador de daño
     */
    private static void add(String attackType, String targetType, double multiplier) {
        chart.computeIfAbsent(attackType, k -> new HashMap<>()).put(targetType, multiplier);
    }

    /**
     * Obtiene el multiplicador de daño según el tipo del ataque y del objetivo.
     *
     * @param attackType tipo del movimiento atacante
     * @param targetType tipo del defensor
     * @return multiplicador de daño (por defecto 1.0 si no está definido)
     */
    public static double getEffectiveness(String attackType, String targetType) {
        return chart.getOrDefault(attackType, Map.of()).getOrDefault(targetType, 1.0);
    }
}
