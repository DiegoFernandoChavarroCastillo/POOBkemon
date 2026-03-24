package presentation.components;

import domain.GameController;
import javax.swing.*;
import java.awt.*;

/**
 * Clase que gestiona la funcionalidad de pausa del juego.
 * Proporciona métodos para pausar y reanudar el juego, mostrando
 * una superposición visual cuando el juego está pausado.
 */
public class PauseManager {
    private JFrame parentFrame;
    private GameController controller;
    private boolean isPaused = false;
    private JPanel pauseOverlay;
    private Font pokemonFont;

    /**
     * Constructor que inicializa el gestor de pausa.
     *
     * @param parentFrame El frame padre donde se mostrará la superposición de pausa
     * @param controller El controlador del juego que maneja la lógica de pausa
     * @param pokemonFont La fuente personalizada para los textos de pausa
     */
    public PauseManager(JFrame parentFrame, GameController controller, Font pokemonFont) {
        this.parentFrame = parentFrame;
        this.controller = controller;
        this.pokemonFont = pokemonFont;
    }

    /**
     * Alterna el estado de pausa del juego.
     * Si no hay una batalla en curso, muestra un mensaje de advertencia.
     */
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

    /**
     * Pausa el juego, detiene el temporizador y muestra la superposición de pausa.
     */
    private void pauseGame() {
        isPaused = true;
        controller.pauseTimer();

        createPauseOverlay();
        parentFrame.setGlassPane(pauseOverlay);
        pauseOverlay.setVisible(true);
    }

    /**
     * Reanuda el juego, oculta la superposición de pausa y reactiva el temporizador.
     */
    private void resumeGame() {
        isPaused = false;

        if (pauseOverlay != null) {
            pauseOverlay.setVisible(false);
        }

        controller.resumeTimer();
    }

    /**
     * Crea el panel de superposición que se muestra cuando el juego está pausado.
     * Incluye un mensaje de "JUEGO PAUSADO" y las instrucciones para continuar.
     */
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

    /**
     * Devuelve el estado actual de pausa del juego.
     *
     * @return true si el juego está pausado, false en caso contrario
     */
    public boolean isPaused() {
        return isPaused;
    }
}