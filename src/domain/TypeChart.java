package domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TypeChart implements Serializable {
    private static final Map<String, Map<String, Double>> chart = new HashMap<>();
    private static final long serialVersionUID = 1L;

    static {
        // ACERO
        add("ACERO", "HADA", 2.0);
        add("ACERO", "HIELO", 2.0);
        add("ACERO", "ROCA", 2.0);
        add("ACERO", "ACERO", 0.5);
        add("ACERO", "AGUA", 0.5);
        add("ACERO", "ELECTR", 0.5);
        add("ACERO", "FUEGO", 0.5);
        add("ACERO", "FANT", 1.0);
        add("ACERO", "LUCHA", 1.0);

        // AGUA
        add("AGUA", "FUEGO", 2.0);
        add("AGUA", "ROCA", 2.0);
        add("AGUA", "TIERRA", 2.0);
        add("AGUA", "AGUA", 0.5);
        add("AGUA", "DRAGON", 0.5);
        add("AGUA", "PLANTA", 0.5);

        // BICHO
        add("BICHO", "PLANTA", 2.0);
        add("BICHO", "PSIQUICO", 2.0);
        add("BICHO", "SINI", 2.0);
        add("BICHO", "FUEGO", 0.5);
        add("BICHO", "HADA", 0.5);
        add("BICHO", "LUCHA", 0.5);
        add("BICHO", "VENENO", 0.5);
        add("BICHO", "ACERO", 0.5);
        add("BICHO", "FANT", 0.5);

        // DRAGON
        add("DRAGON", "DRAGON", 2.0);
        add("DRAGON", "ACERO", 0.5);
        add("DRAGON", "HADA", 0.0);

        // ELECTR
        add("ELECTR", "AGUA", 2.0);
        add("ELECTR", "VOLADOR", 2.0);
        add("ELECTR", "DRAGON", 0.5);
        add("ELECTR", "ELECTR", 0.5);
        add("ELECTR", "PLANTA", 0.5);
        add("ELECTR", "TIERRA", 0.0);

        // FANT
        add("FANT", "FANT", 2.0);
        add("FANT", "PSIQUICO", 2.0);
        add("FANT", "NORMAL", 0.0);
        add("FANT", "SINIESTRO", 0.5);

        // FUEGO
        add("FUEGO", "PLANTA", 2.0);
        add("FUEGO", "HIELO", 2.0);
        add("FUEGO", "BICHO", 2.0);
        add("FUEGO", "ACERO", 2.0);
        add("FUEGO", "AGUA", 0.5);
        add("FUEGO", "DRAGON", 0.5);
        add("FUEGO", "FUEGO", 0.5);
        add("FUEGO", "ROCA", 0.5);

        // HADA
        add("HADA", "DRAGON", 2.0);
        add("HADA", "LUCHA", 2.0);
        add("HADA", "SINI", 2.0);
        add("HADA", "FUEGO", 0.5);
        add("HADA", "VENENO", 0.5);
        add("HADA", "ACERO", 0.5);

        // HIELO
        add("HIELO", "DRAGON", 2.0);
        add("HIELO", "PLANTA", 2.0);
        add("HIELO", "TIERRA", 2.0);
        add("HIELO", "VOLADOR", 2.0);
        add("HIELO", "AGUA", 0.5);
        add("HIELO", "FUEGO", 0.5);
        add("HIELO", "HIELO", 0.5);
        add("HIELO", "ACERO", 0.5);

        // LUCHA
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

    private static void add(String attackType, String targetType, double multiplier) {
        chart.computeIfAbsent(attackType, k -> new HashMap<>()).put(targetType, multiplier);
    }

    public static double getEffectiveness(String attackType, String targetType) {
        return chart.getOrDefault(attackType, Map.of()).getOrDefault(targetType, 1.0);
    }
}
