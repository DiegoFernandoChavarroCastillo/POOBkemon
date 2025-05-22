package presentation;

import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;

/**
 * Diálogo para seleccionar Pokémon que formarán parte del equipo de un entrenador.
 * Permite seleccionar hasta un número máximo especificado de Pokémon desde la base de datos disponible.
 * Permite seleccionar múltiples Pokémon del mismo tipo.
 */
public class PokemonSelectionGUI extends JDialog {
    private List<Pokemon> selectedPokemons;
    private Trainer trainer;
    private JPanel pokemonGrid;
    private JButton confirmButton;
    private int maxPokemons;
    private MoveSelectionCallback moveSelectionCallback;

    // Colores inspirados en Pokémon Esmeralda
    private static final Color EMERALD_GREEN = new Color(0, 150, 136);
    private static final Color EMERALD_DARK_GREEN = new Color(0, 121, 107);
    private static final Color EMERALD_LIGHT_GREEN = new Color(76, 175, 80);
    private static final Color EMERALD_ACCENT = new Color(255, 193, 7);
    private static final Color EMERALD_BACKGROUND = new Color(232, 245, 233);
    private static final Color EMERALD_CARD_BG = new Color(255, 255, 255);
    private static final Color EMERALD_TEXT = new Color(33, 33, 33);
    private static final Color EMERALD_SELECTED = new Color(76, 175, 80);
    private static final Color EMERALD_HOVER = new Color(200, 230, 201);

    // Fuente personalizada
    private Font pokemonFont;
    private Font pokemonFontSmall;
    private Font pokemonFontLarge;

    /**
     * Construye el diálogo PokemonSelectionGUI.
     *
     * @param parent       la ventana principal (padre) del diálogo
     * @param trainer      el entrenador que recibirá los Pokémon seleccionados
     * @param maxPokemons  el número máximo de Pokémon que se pueden seleccionar
     * @param callback     callback que maneja la confirmación de la selección
     */
    public PokemonSelectionGUI(JFrame parent, Trainer trainer, int maxPokemons, MoveSelectionCallback callback) {
        super(parent, "Seleccionar Pokémon", true);
        this.trainer = trainer;
        this.selectedPokemons = new ArrayList<>();
        this.maxPokemons = maxPokemons;
        this.moveSelectionCallback = callback;

        loadCustomFont();
        setupUI();
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Carga la fuente personalizada de Pokémon desde el archivo.
     */
    private void loadCustomFont() {
        try {
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/sprites/pokemon_font.ttf"));
            pokemonFont = baseFont.deriveFont(Font.BOLD, 14f);
            pokemonFontSmall = baseFont.deriveFont(Font.PLAIN, 12f);
            pokemonFontLarge = baseFont.deriveFont(Font.BOLD, 16f);
        } catch (FontFormatException | IOException e) {
            // Si no se puede cargar la fuente personalizada, usar una fuente por defecto
            pokemonFont = new Font("Arial", Font.BOLD, 14);
            pokemonFontSmall = new Font("Arial", Font.PLAIN, 12);
            pokemonFontLarge = new Font("Arial", Font.BOLD, 16);
        }
    }

    /**
     * Configura los componentes de la interfaz gráfica del diálogo.
     * Incluye una grilla con todos los Pokémon disponibles y el botón de confirmación.
     */
    private void setupUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(EMERALD_BACKGROUND);

        // Panel superior con título
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // Configurar grilla de Pokémon
        pokemonGrid = new JPanel(new GridLayout(0, 4, 15, 15));
        pokemonGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        pokemonGrid.setBackground(EMERALD_BACKGROUND);

        for (String name : PokemonDataBase.getAvailablePokemonNames()) {
            Pokemon pokemon = PokemonDataBase.getPokemon(name);
            JPanel pokemonPanel = createPokemonPanel(pokemon);
            pokemonGrid.add(pokemonPanel);
        }

        JScrollPane scrollPane = new JScrollPane(pokemonGrid);
        scrollPane.getViewport().setBackground(EMERALD_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botón de confirmación
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        setMinimumSize(new Dimension(800, 600));
    }

    /**
     * Crea el panel del título con estilo Pokémon Esmeralda.
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(EMERALD_GREEN);
        titlePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, EMERALD_DARK_GREEN),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel("¡Elige tu Equipo Pokémon!");
        titleLabel.setFont(pokemonFontLarge);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel subtitleLabel = new JLabel("Selecciona hasta " + maxPokemons + " Pokémon para tu aventura (Click izq: añadir, Click der: quitar)");
        subtitleLabel.setFont(pokemonFontSmall);
        subtitleLabel.setForeground(new Color(230, 230, 230));
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);

        titlePanel.setLayout(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        return titlePanel;
    }

    /**
     * Crea el panel inferior con el botón de confirmación.
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(EMERALD_BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        confirmButton = new JButton("Confirmar Selección (" + selectedPokemons.size() + "/" + maxPokemons + ")");
        confirmButton.setFont(pokemonFont);
        confirmButton.setEnabled(false);
        styleButton(confirmButton, false);

        confirmButton.addActionListener(e -> {
            if (!selectedPokemons.isEmpty()) {
                for (Pokemon pokemon : selectedPokemons) {
                    trainer.addPokemonToTeam(pokemon);
                }
                dispose();
                moveSelectionCallback.onPokemonSelected(new ArrayList<>(selectedPokemons));
            }
        });

        bottomPanel.add(confirmButton);
        return bottomPanel;
    }

    /**
     * Aplica estilo a los botones según el tema de Pokémon Esmeralda.
     */
    private void styleButton(JButton button, boolean isEnabled) {
        if (isEnabled) {
            button.setBackground(EMERALD_GREEN);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
        } else {
            button.setBackground(Color.GRAY);
            button.setForeground(Color.LIGHT_GRAY);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLoweredBevelBorder(),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
        }
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Crea el panel visual para un Pokémon, con su imagen y nombre.
     * Permite seleccionar múltiples Pokémon del mismo tipo con clic izquierdo y deseleccionar con clic derecho.
     *
     * @param pokemon el Pokémon a representar gráficamente
     * @return un panel listo para ser agregado a la grilla
     */
    private JPanel createPokemonPanel(Pokemon pokemon) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EMERALD_CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Panel para la imagen
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        imagePanel.setBackground(EMERALD_CARD_BG);

        try {
            ImageIcon icon = new ImageIcon("src/sprites/" + pokemon.getName().toLowerCase() + "_front.png");
            Image scaledImage = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imagePanel.add(imageLabel);
        } catch (Exception e) {
            JLabel placeholderLabel = new JLabel("🔵", JLabel.CENTER);
            placeholderLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
            placeholderLabel.setForeground(EMERALD_GREEN);
            imagePanel.add(placeholderLabel);
        }

        panel.add(imagePanel, BorderLayout.CENTER);

        // Panel para el nombre con contador de selecciones
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setBackground(EMERALD_LIGHT_GREEN);
        namePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, EMERALD_GREEN),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JLabel nameLabel = new JLabel(pokemon.getName(), JLabel.CENTER);
        nameLabel.setFont(pokemonFont);
        nameLabel.setForeground(Color.WHITE);
        namePanel.add(nameLabel, BorderLayout.CENTER);

        // Label para mostrar la cantidad seleccionada
        JLabel countLabel = new JLabel("", JLabel.CENTER);
        countLabel.setFont(pokemonFontSmall);
        countLabel.setForeground(Color.WHITE);
        namePanel.add(countLabel, BorderLayout.SOUTH);

        panel.add(namePanel, BorderLayout.SOUTH);

        // Efectos de hover
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                int selectedCount = getSelectedCount(pokemon.getName());
                if (selectedCount == 0) {
                    panel.setBackground(EMERALD_HOVER);
                    imagePanel.setBackground(EMERALD_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                int selectedCount = getSelectedCount(pokemon.getName());
                if (selectedCount == 0) {
                    panel.setBackground(EMERALD_CARD_BG);
                    imagePanel.setBackground(EMERALD_CARD_BG);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // Click izquierdo: añadir Pokémon
                    if (selectedPokemons.size() < maxPokemons) {
                        Pokemon clonedPokemon = pokemon.clone();
                        selectedPokemons.add(clonedPokemon);
                        updatePokemonPanelStyle(panel, imagePanel, namePanel, countLabel, pokemon.getName());

                        // Animación de selección
                        Timer timer = new Timer(100, null);
                        timer.addActionListener(ae -> {
                            int count = getSelectedCount(pokemon.getName());
                            if (count > 0) {
                                panel.setBorder(BorderFactory.createCompoundBorder(
                                        BorderFactory.createLineBorder(EMERALD_SELECTED, 3),
                                        BorderFactory.createEmptyBorder(7, 7, 7, 7)
                                ));
                            }
                            timer.stop();
                        });
                        timer.start();
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // Click derecho: quitar un Pokémon de este tipo
                    Pokemon toRemove = null;
                    for (Pokemon p : selectedPokemons) {
                        if (p.getName().equals(pokemon.getName())) {
                            toRemove = p;
                            break;
                        }
                    }
                    if (toRemove != null) {
                        selectedPokemons.remove(toRemove);
                        updatePokemonPanelStyle(panel, imagePanel, namePanel, countLabel, pokemon.getName());
                    }
                }

                updateConfirmButton();
            }
        });

        // Actualizar el estilo inicial
        updatePokemonPanelStyle(panel, imagePanel, namePanel, countLabel, pokemon.getName());

        return panel;
    }

    /**
     * Obtiene la cantidad de Pokémon seleccionados de un tipo específico.
     */
    private int getSelectedCount(String pokemonName) {
        return (int) selectedPokemons.stream()
                .filter(p -> p.getName().equals(pokemonName))
                .count();
    }

    /**
     * Actualiza el estilo visual del panel de un Pokémon según la cantidad seleccionada.
     */
    private void updatePokemonPanelStyle(JPanel panel, JPanel imagePanel, JPanel namePanel, JLabel countLabel, String pokemonName) {
        int selectedCount = getSelectedCount(pokemonName);

        if (selectedCount > 0) {
            panel.setBackground(new Color(220, 248, 198));
            imagePanel.setBackground(new Color(220, 248, 198));
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(EMERALD_SELECTED, 3),
                    BorderFactory.createEmptyBorder(7, 7, 7, 7)
            ));
            namePanel.setBackground(EMERALD_SELECTED);
            countLabel.setText("x" + selectedCount);
            countLabel.setVisible(true);
        } else {
            panel.setBackground(EMERALD_CARD_BG);
            imagePanel.setBackground(EMERALD_CARD_BG);
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            namePanel.setBackground(EMERALD_LIGHT_GREEN);
            countLabel.setText("");
            countLabel.setVisible(false);
        }
        panel.repaint();
    }

    /**
     * Actualiza el estado y texto del botón de confirmación.
     */
    private void updateConfirmButton() {
        confirmButton.setText("Confirmar Selección (" + selectedPokemons.size() + "/" + maxPokemons + ")");
        boolean enabled = !selectedPokemons.isEmpty();
        confirmButton.setEnabled(enabled);
        styleButton(confirmButton, enabled);

        if (enabled) {
            confirmButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    confirmButton.setBackground(EMERALD_DARK_GREEN);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    confirmButton.setBackground(EMERALD_GREEN);
                }
            });
        }
    }

    /**
     * Interfaz funcional para manejar la acción posterior a la selección de Pokémon.
     */
    public interface MoveSelectionCallback {
        /**
         * Se ejecuta cuando el usuario ha confirmado la selección de Pokémon.
         *
         * @param pokemons la lista de Pokémon seleccionados
         */
        void onPokemonSelected(List<Pokemon> pokemons);
    }
}