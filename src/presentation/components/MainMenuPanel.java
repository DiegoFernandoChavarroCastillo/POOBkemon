package presentation.components;

import domain.GameController;
import javax.swing.*;
import java.awt.*;

/**
 * Panel principal del menú del juego que muestra las opciones de inicio.
 * Contiene el logo del juego, botones para seleccionar modos de juego
 * y un panel informativo.
 */
public class MainMenuPanel extends JPanel {
    private GameController controller;
    private Font pokemonFont;
    private SpriteManager spriteManager;

    /**
     * Constructor que inicializa el panel del menú principal.
     *
     * @param controller El controlador del juego que maneja las acciones
     * @param pokemonFont La fuente personalizada para los elementos del menú
     */
    public MainMenuPanel(GameController controller, Font pokemonFont) {
        this.controller = controller;
        this.pokemonFont = pokemonFont;
        this.spriteManager = new SpriteManager();
        initializeMenu();
    }

    /**
     * Inicializa los componentes del menú y su disposición.
     * Configura el diseño, color de fondo y añade los paneles principales.
     */
    private void initializeMenu() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(120, 200, 80));

        add(createLogoPanel(), BorderLayout.NORTH);
        add(createButtonPanel(), BorderLayout.CENTER);
        add(createInfoPanel(), BorderLayout.SOUTH);
    }

    /**
     * Crea el panel que contiene el logo del juego.
     * Intenta cargar una imagen del logo usando SpriteManager, y si falla,
     * muestra un título de texto alternativo.
     *
     * @return JPanel configurado como panel del logo
     */
    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(new Color(120, 200, 80));

        JLabel logoLabel = new JLabel();
        if (spriteManager.tryLoadSprite(logoLabel, "Logo", 200, 100)) {
            logoPanel.add(logoLabel);
        } else {
            JLabel titleLabel = new JLabel("¡Bienvenido a POOBkemon Battle!", JLabel.CENTER);
            titleLabel.setFont(pokemonFont.deriveFont(Font.BOLD, 24));
            titleLabel.setForeground(Color.WHITE);
            logoPanel.add(titleLabel);
        }

        return logoPanel;
    }

    /**
     * Crea el panel que contiene los botones de selección de modo de juego.
     *
     * @return JPanel configurado con los botones de modo de juego
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.setBackground(new Color(120, 200, 80));

        buttonPanel.add(createModeButton("Jugador vs Jugador (PvP)",
                new Color(200, 60, 60), () -> controller.showPlayerSetup(1)));
        buttonPanel.add(createModeButton("Jugador vs Máquina (PvM)",
                new Color(60, 140, 200), () -> controller.showPlayerSetup(2)));
        buttonPanel.add(createModeButton("Máquina vs Máquina (MvM)",
                new Color(200, 160, 60), () -> controller.showPlayerSetup(3)));

        return buttonPanel;
    }

    /**
     * Crea un botón personalizado para los modos de juego.
     *
     * @param text El texto que mostrará el botón
     * @param bgColor El color de fondo del botón
     * @param action La acción que se ejecutará al hacer clic
     * @return JButton configurado con las propiedades especificadas
     */
    private JButton createModeButton(String text, Color bgColor, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(pokemonFont.deriveFont(Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        button.addActionListener(e -> action.run());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Crea el panel inferior con información adicional.
     *
     * @return JPanel configurado como panel informativo
     */
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(80, 160, 200));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 80, 100), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel infoLabel = new JLabel("<html><div style='text-align:center;color:white;'>" +
                "<b>Bienvenidos :)</b><br><br></div></html>");
        infoLabel.setFont(pokemonFont);
        infoPanel.add(infoLabel);

        return infoPanel;
    }
}