package presentation.components;

import domain.*;
import javax.swing.*;
import java.util.List;

/**
 * Clase encargada de gestionar la persistencia de partidas del juego,
 * proporcionando métodos para guardar y cargar estados del juego.
 * Utiliza cuadros de diálogo para interactuar con el usuario.
 */
public class GamePersistenceManager {
    private JFrame parentFrame;

    /**
     * Constructor que inicializa el gestor de persistencia con el frame padre.
     *
     * @param parentFrame El frame padre que se utilizará para mostrar los diálogos
     */
    public GamePersistenceManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    /**
     * Guarda el estado actual del juego en un archivo.
     * Muestra diálogos para solicitar el nombre del archivo y notifica el resultado.
     *
     * @param controller El controlador del juego que contiene el estado actual
     * @param gameMode El modo de juego actual que se desea guardar
     */
    public void saveGame(GameController controller, int gameMode) {
        if (controller.getCurrentBattle() == null) {
            JOptionPane.showMessageDialog(parentFrame, "No hay partida en curso para guardar");
            return;
        }

        String filename = JOptionPane.showInputDialog(parentFrame, "Nombre para guardar la partida:");
        if (filename != null && !filename.trim().isEmpty()) {
            boolean success = PersistenceManager.saveGame(
                    new GameState(
                            controller.getCurrentBattle(),
                            gameMode,
                            controller.getCurrentBattle().getPlayer1().getName(),
                            controller.getCurrentBattle().getPlayer2().getName()
                    ),
                    filename
            );

            String message = success ? "Partida guardada exitosamente" : "Error al guardar la partida";
            int messageType = success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog(parentFrame, message, "Guardar", messageType);
        }
    }

    /**
     * Carga un estado del juego previamente guardado.
     * Muestra una lista de partidas guardadas disponibles y permite seleccionar una.
     *
     * @return El estado del juego cargado, o null si no se completó la operación
     */
    public GameState loadGame() {
        List<String> savedGames = PersistenceManager.getSavedGames();
        if (savedGames.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "No hay partidas guardadas");
            return null;
        }

        String selected = (String) JOptionPane.showInputDialog(
                parentFrame,
                "Selecciona una partida para cargar:",
                "Cargar partida",
                JOptionPane.PLAIN_MESSAGE,
                null,
                savedGames.toArray(),
                savedGames.get(0)
        );

        if (selected != null) {
            GameState gameState = PersistenceManager.loadGame(selected);
            if (gameState != null) {
                JOptionPane.showMessageDialog(parentFrame, "Partida cargada exitosamente");
                return gameState;
            }
        }
        return null;
    }
}