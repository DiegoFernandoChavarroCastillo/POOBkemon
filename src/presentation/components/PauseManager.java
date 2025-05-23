package presentation.components;

import domain.GameController;
import javax.swing.*;
import java.awt.*;

public class PauseManager {
    private JFrame parentFrame;
    private GameController controller;
    private boolean isPaused = false;
    private JPanel pauseOverlay;
    private Font pokemonFont;

    public PauseManager(JFrame parentFrame, GameController controller, Font pokemonFont) {
        this.parentFrame = parentFrame;
        this.controller = controller;
        this.pokemonFont = pokemonFont;
    }

    public void togglePause() {
        if (controller == null || controller.getCurrentBattle() == null) {
            JOptionPane.showMessageDialog(parentFrame, "No hay batalla en curso para pausar");
            return;
        }

        if (isPaused) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    private void pauseGame() {
        isPaused = true;
        controller.pauseTimer();

        createPauseOverlay();
        parentFrame.setGlassPane(pauseOverlay);
        pauseOverlay.setVisible(true);
    }

    private void resumeGame() {
        isPaused = false;

        if (pauseOverlay != null) {
            pauseOverlay.setVisible(false);
        }

        controller.resumeTimer();
    }

    private void createPauseOverlay() {
        pauseOverlay = new JPanel();
        pauseOverlay.setBackground(new Color(0, 0, 0, 150));
        pauseOverlay.setLayout(new BorderLayout());

        JLabel pauseLabel = new JLabel("JUEGO PAUSADO", JLabel.CENTER);
        pauseLabel.setFont(pokemonFont.deriveFont(Font.BOLD, 48));
        pauseLabel.setForeground(Color.WHITE);

        JLabel instructionLabel = new JLabel("Presiona 'Pausa' en el menú para continuar", JLabel.CENTER);
        instructionLabel.setFont(pokemonFont.deriveFont(Font.PLAIN, 16));
        instructionLabel.setForeground(Color.LIGHT_GRAY);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(pauseLabel);
        centerPanel.add(instructionLabel);

        pauseOverlay.add(centerPanel, BorderLayout.CENTER);
    }

    public boolean isPaused() {
        return isPaused;
    }
}