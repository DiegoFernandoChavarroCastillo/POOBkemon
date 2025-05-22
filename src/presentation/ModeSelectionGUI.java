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

    // Colores inspirados en Pokémon Emerald
    private static final Color EMERALD_GREEN = new Color(16, 147, 74);
    private static final Color DARK_GREEN = new Color(10, 100, 50);
    private static final Color LIGHT_GREEN = new Color(156, 230, 156);
    private static final Color CREAM = new Color(248, 248, 208);
    private static final Color DARK_BLUE = new Color(24, 40, 120);
    private static final Color BUTTON_BLUE = new Color(72, 120, 248);
    private static final Color BUTTON_RED = new Color(248, 88, 72);
    private static final Color BORDER_DARK = new Color(88, 104, 128);

    private Font pokemonFont;

    /**
     * Constructor de la pantalla de selección de modo.
     *
     * @param gui Referencia al BattleGUI principal para continuar el flujo de la aplicación
     */
    public ModeSelectionGUI(BattleGUI gui) {
        setTitle("POOBkemon Battle - Seleccionar modo de juego");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
        setResizable(false);

        // Cargar la fuente Pokémon
        loadPokemonFont();

        // Panel principal con gradiente
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Panel superior con logo y título
        JPanel topPanel = createTopPanel();

        // Panel central con botones
        JPanel centerPanel = createCenterPanel(gui);

        // Panel inferior con información
        JPanel bottomPanel = createBottomPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Carga la fuente Pokémon desde el archivo.
     */
    private void loadPokemonFont() {
        try {
            File fontFile = new File("src/sprites/pokemon_font.ttf");
            if (fontFile.exists()) {
                Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                pokemonFont = baseFont.deriveFont(Font.BOLD, 24f);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(baseFont);
            } else {
                // Fuente de respaldo
                pokemonFont = new Font("Dialog", Font.BOLD, 24);
                System.out.println("Fuente Pokémon no encontrada, usando fuente de respaldo");
            }
        } catch (Exception e) {
            pokemonFont = new Font("Dialog", Font.BOLD, 24);
            System.err.println("Error al cargar la fuente Pokémon: " + e.getMessage());
        }
    }

    /**
     * Crea el panel superior con logo y título.
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Panel para el logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);

        try {
            BufferedImage logoImage = ImageIO.read(new File("src/sprites/Logo.png"));
            Image scaledLogo = logoImage.getScaledInstance(350, 120, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));

            // Añadir borde estilo Pokémon al logo
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

        // Título descriptivo
        JLabel subtitleLabel = createStyledLabel("¡Selecciona tu modo de aventura!",
                pokemonFont.deriveFont(20f), CREAM);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(logoPanel);
        topPanel.add(Box.createVerticalStrut(15));
        topPanel.add(subtitleLabel);

        return topPanel;
    }

    /**
     * Crea el panel central con los botones de selección.
     */
    private JPanel createCenterPanel(BattleGUI gui) {
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Botón Modo Normal
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

        // Botón Modo Supervivencia
        JButton survivalButton = createPokemonButton(
                "MODO SUPERVIVENCIA",
                "¡Equipos aleatorios y desafío extremo!",
                BUTTON_RED,
                "src/sprites/masterball_icon.png"
        );

        survivalButton.addActionListener(e -> {
            playButtonSound();
            gui.setGameMode(GameController.MODO_SUPERVIVENCIA);
            dispose();
            gui.startSurvivalGame();
        });

        // Posicionar botones
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

    /**
     * Crea el panel inferior con información adicional.
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new EmeraldInfoPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DARK, 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel infoLabel = new JLabel(
                "<html><div style='text-align:center;'>" +
                        "<b>¡Bienvenido a la región de Hoenn!</b><br>" +
                        "Prepárate para vivir aventuras épicas con tus POOBkemon<br>" +
                        "<i>Usa las teclas de dirección para navegar</i>" +
                        "</div></html>"
        );
        infoLabel.setFont(pokemonFont.deriveFont(14f));
        infoLabel.setForeground(DARK_BLUE);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bottomPanel.add(infoLabel, BorderLayout.CENTER);
        return bottomPanel;
    }

    /**
     * Crea un botón con estilo Pokémon Emerald.
     */
    private JButton createPokemonButton(String title, String description, Color baseColor, String iconPath) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradiente de fondo
                GradientPaint gradient = new GradientPaint(
                        0, 0, baseColor.brighter(),
                        0, getHeight(), baseColor
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Borde
                g2d.setColor(BORDER_DARK);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 15, 15);

                // Efecto de brillo superior
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

        // Panel de contenido
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Icono (si existe)
        JLabel iconLabel = null;
        try {
            BufferedImage iconImage = ImageIO.read(new File(iconPath));
            Image scaledIcon = iconImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            iconLabel = new JLabel(new ImageIcon(scaledIcon));
        } catch (Exception e) {
            // Si no se puede cargar el icono, usar un círculo coloreado
            iconLabel = new JLabel("●");
            iconLabel.setFont(new Font("Dialog", Font.BOLD, 30));
            iconLabel.setForeground(Color.WHITE);
        }

        // Textos
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

        // Efectos de hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                // Efecto de escalado sutil
                button.setBorder(BorderFactory.createEmptyBorder(-2, -2, -2, -2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                button.setBorder(null);
            }
        });

        return button;
    }

    /**
     * Crea una etiqueta con estilo personalizado.
     */
    private JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(font);
        label.setForeground(color);

        // Efecto de sombra para el texto
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10),
                BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 1)
        ));
        label.setOpaque(true);
        label.setBackground(new Color(0, 0, 0, 30));

        return label;
    }

    /**
     * Simula el sonido de botón (placeholder).
     */
    private void playButtonSound() {
        // Aquí podrías añadir la reproducción de un sonido
        System.out.println("♪ Sonido de botón Pokémon ♪");
    }

    /**
     * Panel con gradiente de fondo estilo Pokémon Emerald.
     */
    private class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Gradiente principal
            GradientPaint gradient = new GradientPaint(
                    0, 0, LIGHT_GREEN,
                    0, getHeight(), EMERALD_GREEN
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Patrón de puntos decorativo
            g2d.setColor(new Color(255, 255, 255, 30));
            for (int x = 0; x < getWidth(); x += 50) {
                for (int y = 0; y < getHeight(); y += 50) {
                    g2d.fillOval(x, y, 4, 4);
                }
            }
        }
    }

    /**
     * Panel de información con estilo Emerald.
     */
    private class EmeraldInfoPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo con gradiente sutil
            GradientPaint gradient = new GradientPaint(
                    0, 0, CREAM,
                    0, getHeight(), new Color(240, 240, 200)
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        }
    }
}