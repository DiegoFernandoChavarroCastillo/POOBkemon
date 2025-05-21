package domain;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de manejar la persistencia del juego (guardar/cargar partidas)
 */
public class PersistenceManager {
    private static final String SAVE_FOLDER = "saves/";

    static {
        // Crear la carpeta de guardados si no existe
        new File(SAVE_FOLDER).mkdirs();
    }

    /**
     * Guarda el estado del juego en un archivo
     * @param gameState Estado del juego a guardar
     * @param filename Nombre del archivo (sin extensión)
     * @return true si se guardó correctamente
     */
    public static boolean saveGame(GameState gameState, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_FOLDER + filename + ".dat"))) {
            oos.writeObject(gameState);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar la partida: " + e.getMessage());
            return false;
        }
    }

    /**
     * Carga un estado del juego desde un archivo
     * @param filename Nombre del archivo (sin extensión)
     * @return Objeto GameState o null si hubo error
     */
    public static GameState loadGame(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(SAVE_FOLDER + filename + ".dat"))) {
            return (GameState) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar la partida: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene la lista de partidas guardadas
     * @return Lista de nombres de archivos guardados
     */
    public static List<String> getSavedGames() {
        File folder = new File(SAVE_FOLDER);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".dat"));
        List<String> savedGames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                savedGames.add(file.getName().replace(".dat", ""));
            }
        }
        return savedGames;
    }
}