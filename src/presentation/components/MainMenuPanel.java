package presentation.components;

import domain.GameController;
import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {
    private GameController controller;
    private Font pokemonFont;
    private SpriteManager spriteManager;

    public MainMenuPanel(GameController controller, Font pokemonFont) {
        this.controller = controller;
        this.pokemonFont = pokemonFont;
        this.spriteManager = new SpriteManager();
        initializeMenu();
    }

    private void initializeMenu() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(120, 200, 80));

        add(createLogoPanel(), BorderLayout.NORTH);
        add(createButtonPanel(), BorderLayout.CENTER);
        add(createInfoPanel(), BorderLayout.SOUTH);
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(new Color(120, 200, 80));

        // Usar SpriteManager para cargar logo
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

    private JButton createModeButton(String text, Color bgColor, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(pokemonFont.deriveFont(Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        button.addActionListener(e -> action.run());

        // Efectos hover
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