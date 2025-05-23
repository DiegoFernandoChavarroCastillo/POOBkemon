package presentation.components;

import domain.*;
import javax.swing.*;
import java.util.List;

public class GamePersistenceManager {
    private JFrame parentFrame;

    public GamePersistenceManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

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