package presentation;

import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Pantalla inicial que permite seleccionar el modo de juego: Normal o Supervivencia.
 * Esta pantalla aparece al iniciar la aplicación, antes del menú principal.
 * Diseñada con estilo Pokémon Emerald.
 */
public class ModeSelectionGUI extends JFrame {

    // Colores inspirados en Pokémon Emerald (actualizados para mayor fidelidad)
    private static final Color EMERALD_GREEN = new Color(0, 200, 145);
    private static final Color DARK_GREEN = new Color(0, 130, 100);
    private static final Color LIGHT_GREEN = new Color(200, 255, 200);
    private static final Color CREAM = new Color(255, 255, 220);
    private static final Color DARK_BLUE = new Color(16, 24, 48);
    private static final Color BUTTON_BLUE = new Color(80, 160, 255);
    private static final Color BUTTON_RED = new Color(255, 80, 80);
    private static final Color BORDER_DARK = new Color(32, 64, 64);

    private Font pokemonFont;

    public ModeSelectionGUI(BattleGUI gui) {
        setTitle("POOBkemon Battle - Seleccionar modo de juego");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
        setResizable(false);

        loadPokemonFont();

        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel topPanel = createTopPanel();
        JPanel centerPanel = createCenterPanel(gui);
        JPanel bottomPanel = createBottomPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadPokemonFont() {
        try {
            File fontFile = new File("src/sprites/pokemon_font.ttf");
            if (fontFile.exists()) {
                Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                pokemonFont = baseFont.deriveFont(Font.BOLD, 24f);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(baseFont);
            } else {
                pokemonFont = new Font("Dialog", Font.BOLD, 24);
                System.out.println("Fuente Pokémon no encontrada, usando fuente de respaldo");
            }
        } catch (Exception e) {
            pokemonFont = new Font("Dialog", Font.BOLD, 24);
            System.err.println("Error al cargar la fuente Pokémon: " + e.getMessage());
        }
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);

        try {
            BufferedImage logoImage = ImageIO.read(new File("src/sprites/Logo.png"));
            Image scaledLogo = logoImage.getScaledInstance(350, 120, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            logoLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_DARK, 3),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            logoLabel.setOpaque(true);
            logoLabel.setBackground(CREAM);
            logoPanel.add(logoLabel);
        } catch (IOException e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
            JLabel titleLabel = createStyledLabel("POOBkemon Battle", pokemonFont.deriveFont(36f), CREAM);
            logoPanel.add(titleLabel);
        }

        JLabel subtitleLabel = createStyledLabel("¡Selecciona tu modo de aventura!",
                pokemonFont.deriveFont(20f), CREAM);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(logoPanel);
        topPanel.add(Box.createVerticalStrut(15));
        topPanel.add(subtitleLabel);

        return topPanel;
    }

    private JPanel createCenterPanel(BattleGUI gui) {
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JButton normalButton = createPokemonButton(
                "MODO NORMAL",
                "Configura tu equipo y estrategia",
                BUTTON_BLUE,
                "src/sprites/pokeball_icon.png"
        );

        normalButton.addActionListener(e -> {
            playButtonSound();
            gui.setGameMode(GameController.MODO_NORMAL);
            dispose();
            gui.showGameModeSelection();
        });

        JButton survivalButton = createPokemonButton(
                "MODO SUPERVIVENCIA",
                "¡Equipos aleatorios y desafio extremo!",
                BUTTON_RED,
                "src/sprites/masterball_icon.png"
        );

        survivalButton.addActionListener(e -> {
            playButtonSound();
            gui.setGameMode(GameController.MODO_SUPERVIVENCIA);
            dispose();
            gui.startSurvivalGame();
        });

        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(normalButton, gbc);

        gbc.gridy = 1;
        centerPanel.add(survivalButton, gbc);

        return centerPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new EmeraldInfoPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DARK, 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel infoLabel = new JLabel(
                "<html><div style='text-align:center;'>" +
                        "<b>¡Bienvenido a la region de Hoenn!</b><br>" +
                        "Preparate para vivir aventuras epicas con tus POOBkemon<br>" + "</div></html>"
        );
        infoLabel.setFont(pokemonFont.deriveFont(14f));
        infoLabel.setForeground(DARK_BLUE);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bottomPanel.add(infoLabel, BorderLayout.CENTER);
        return bottomPanel;
    }

    private JButton createPokemonButton(String title, String description, Color baseColor, String iconPath) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, baseColor.brighter(),
                        0, getHeight(), baseColor
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(BORDER_DARK);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 15, 15);
                g2d.setColor(new Color(255, 255, 255, 80));
                g2d.fillRoundRect(5, 5, getWidth()-10, getHeight()/3, 10, 10);
                g2d.dispose();
            }
        };

        button.setLayout(new BorderLayout());
        button.setPreferredSize(new Dimension(500, 120));
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel iconLabel = null;
        try {
            BufferedImage iconImage = ImageIO.read(new File(iconPath));
            Image scaledIcon = iconImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            iconLabel = new JLabel(new ImageIcon(scaledIcon));
        } catch (Exception e) {
            iconLabel = new JLabel("●");
            iconLabel.setFont(new Font("Dialog", Font.BOLD, 30));
            iconLabel.setForeground(Color.WHITE);
        }

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(pokemonFont.deriveFont(18f));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(pokemonFont.deriveFont(12f));
        descLabel.setForeground(new Color(255, 255, 255, 200));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descLabel);

        contentPanel.add(iconLabel, BorderLayout.WEST);
        contentPanel.add(Box.createHorizontalStrut(15), BorderLayout.CENTER);
        contentPanel.add(textPanel, BorderLayout.CENTER);

        button.add(contentPanel);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.setBorder(BorderFactory.createEmptyBorder(-2, -2, -2, -2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                button.setBorder(null);
            }
        });

        return button;
    }

    private JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(font);
        label.setForeground(color);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10),
                BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 1)
        ));
        label.setOpaque(true);
        label.setBackground(new Color(0, 0, 0, 30));
        return label;
    }

    private void playButtonSound() {
        System.out.println("♪ Sonido de botón Pokémon ♪");
    }

    private class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gradient = new GradientPaint(
                    0, 0, LIGHT_GREEN,
                    0, getHeight(), EMERALD_GREEN
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(new Color(255, 255, 255, 30));
            for (int x = 0; x < getWidth(); x += 50) {
                for (int y = 0; y < getHeight(); y += 50) {
                    g2d.fillOval(x, y, 4, 4);
                }
            }
        }
    }

    private class EmeraldInfoPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gradient = new GradientPaint(
                    0, 0, CREAM,
                    0, getHeight(), new Color(240, 255, 240)
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        }
    }
}
