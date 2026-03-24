package presentation;

import domain.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Diálogo para seleccionar movimientos para un Pokémon.
 * Permite al usuario elegir exactamente 4 movimientos desde la base de datos de movimientos disponibles.
 */
public class MoveSelectionGUI extends JDialog {
    private Pokemon pokemon;
    private List<Move> selectedMoves;
    private JPanel movesPanel;
    private JButton confirmButton;
    private JLabel pokemonInfoLabel;
    private Font pokemonFont;

    // Colores del tema Pokémon Emerald
    private static final Color EMERALD_GREEN = new Color(80, 200, 120);
    private static final Color DARK_GREEN = new Color(40, 120, 60);
    private static final Color LIGHT_GREEN = new Color(152, 251, 152);
    private static final Color CREAM = new Color(248, 248, 220);
    private static final Color GOLD = new Color(255, 215, 0);

    /**
     * Construye el diálogo MoveSelectionGUI.
     *
     * @param parent  la ventana principal (padre) del diálogo
     * @param pokemon el Pokémon que recibirá los movimientos seleccionados
     */
    public MoveSelectionGUI(JFrame parent, Pokemon pokemon) {
        super(parent, "Seleccionar Movimientos para " + pokemon.getName(), true);
        this.pokemon = pokemon;
        this.selectedMoves = new ArrayList<>();

        loadPokemonFont();
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setupUI();

        // Establecer el fondo del diálogo
        getContentPane().setBackground(LIGHT_GREEN);
    }

    /**
     * Carga la fuente personalizada de Pokémon.
     */
    private void loadPokemonFont() {
        try {
            File fontFile = new File("src/sprites/pokemon_font.ttf");
            if (fontFile.exists()) {
                pokemonFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(pokemonFont);
            } else {
                // Fuente de respaldo si no se encuentra la fuente personalizada
                pokemonFont = new Font("Arial", Font.BOLD, 12);
            }
        } catch (FontFormatException | IOException e) {
            pokemonFont = new Font("Arial", Font.BOLD, 12);
        }
    }

    /**
     * Configura la interfaz gráfica del diálogo.
     * Establece paneles, etiquetas y botones.
     */
    private void setupUI() {
        setLayout(new BorderLayout(15, 15));

        // Panel superior con información del Pokémon
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Panel central con los movimientos
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Crea el panel superior con la información del Pokémon.
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(DARK_GREEN);
        topPanel.setBorder(createEmeraldBorder());

        // Título principal
        JLabel titleLabel = new JLabel("SELECCIÓN DE MOVIMIENTOS", JLabel.CENTER);
        titleLabel.setFont(pokemonFont.deriveFont(Font.BOLD, 18f));
        titleLabel.setForeground(GOLD);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        // Información del Pokémon
        pokemonInfoLabel = new JLabel(getPokemonInfoText(), JLabel.CENTER);
        pokemonInfoLabel.setFont(pokemonFont.deriveFont(Font.BOLD, 14f));
        pokemonInfoLabel.setForeground(CREAM);
        pokemonInfoLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(pokemonInfoLabel, BorderLayout.CENTER);

        return topPanel;
    }

    /**
     * Crea el panel central con los movimientos disponibles.
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(LIGHT_GREEN);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Etiqueta de instrucciones
        JLabel instructionLabel = new JLabel("Selecciona exactamente 4 movimientos:", JLabel.CENTER);
        instructionLabel.setFont(pokemonFont.deriveFont(Font.BOLD, 12f));
        instructionLabel.setForeground(DARK_GREEN);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        movesPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        movesPanel.setBackground(LIGHT_GREEN);
        movesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(movesPanel);
        scrollPane.setBorder(createEmeraldBorder());
        scrollPane.getViewport().setBackground(LIGHT_GREEN);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        centerPanel.add(instructionLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        loadAvailableMoves();
        return centerPanel;
    }

    /**
     * Crea el panel inferior con los botones de acción.
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(LIGHT_GREEN);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        confirmButton = createStyledButton("Confirmar (" + selectedMoves.size() + "/4)", EMERALD_GREEN);
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> confirmSelection());

        JButton cancelButton = createStyledButton("Cancelar", new Color(220, 80, 80));
        cancelButton.addActionListener(e -> dispose());

        bottomPanel.add(cancelButton);
        bottomPanel.add(confirmButton);

        return bottomPanel;
    }

    /**
     * Crea un botón con estilo Pokémon Emerald.
     */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(pokemonFont.deriveFont(Font.BOLD, 12f));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(createButtonBorder());
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 35));

        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(backgroundColor.brighter());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(backgroundColor);
                }
            }
        });

        return button;
    }

    /**
     * Crea un borde estilo Pokémon Emerald.
     */
    private Border createEmeraldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(DARK_GREEN, 2),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                )
        );
    }

    /**
     * Crea un borde para botones estilo Pokémon Emerald.
     */
    private Border createButtonBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        );
    }

    /**
     * Retorna el texto HTML con la información del Pokémon.
     *
     * @return cadena de texto con nombre, nivel y tipo del Pokémon
     */
    private String getPokemonInfoText() {
        return "<html><center><b>" + pokemon.getName() + "</b><br>" +
                "Nivel " + pokemon.getLevel() + " • Tipo: " + pokemon.getType() + "</center></html>";
    }

    /**
     * Carga los movimientos disponibles desde la base de datos.
     * Ordena alfabéticamente y los muestra como botones.
     */
    private void loadAvailableMoves() {
        List<Move> availableMoves = MoveDatabase.getAvailableMoves();
        availableMoves.sort(Comparator.comparing(Move::name));

        for (Move move : availableMoves) {
            JButton moveButton = createMoveButton(move);
            movesPanel.add(moveButton);
        }

        movesPanel.revalidate();
    }

    /**
     * Crea un botón gráfico con la información del movimiento.
     *
     * @param move el movimiento a mostrar
     * @return botón configurado
     */
    private JButton createMoveButton(Move move) {
        String buttonText = "<html><center><b>" + move.name() + "</b><br>" +
                "<small>Tipo: " + move.type() + "</small><br>" +
                "<small>Poder: " + move.power() + " • PP: " + move.maxPP() + "</small></center></html>";

        JButton button = new JButton(buttonText);
        button.setFont(pokemonFont.deriveFont(10f));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setBackground(getMoveTypeColor(move.type()));
        button.setForeground(Color.WHITE);
        button.setBorder(createMoveBorder(false));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 80));

        // Efecto hover
        Color originalColor = getMoveTypeColor(move.type());
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!selectedMoves.contains(move)) {
                    button.setBackground(originalColor.brighter());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!selectedMoves.contains(move)) {
                    button.setBackground(originalColor);
                }
            }
        });

        button.addActionListener(e -> toggleMoveSelection(move, button));
        return button;
    }

    /**
     * Crea un borde para los botones de movimiento.
     */
    private Border createMoveBorder(boolean selected) {
        if (selected) {
            return BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GOLD, 3),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createRaisedBevelBorder(),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)
                    )
            );
        } else {
            return BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            );
        }
    }

    /**
     * Asigna un color representativo según el tipo del movimiento.
     *
     * @param type el tipo del movimiento
     * @return color asociado al tipo
     */
    private Color getMoveTypeColor(String type) {
        switch (type.toUpperCase()) {
            case "FIRE": return new Color(240, 80, 50);
            case "WATER": return new Color(80, 140, 220);
            case "ELECTRIC": return new Color(248, 208, 48);
            case "GRASS": return new Color(120, 200, 80);
            case "ICE": return new Color(160, 220, 240);
            case "FIGHTING": return new Color(160, 80, 50);
            case "POISON": return new Color(160, 64, 160);
            case "GROUND": return new Color(224, 192, 104);
            case "FLYING": return new Color(168, 144, 240);
            case "PSYCHIC": return new Color(248, 88, 136);
            case "BUG": return new Color(168, 184, 32);
            case "ROCK": return new Color(184, 160, 56);
            case "GHOST": return new Color(112, 88, 152);
            case "DRAGON": return new Color(112, 56, 248);
            case "DARK": return new Color(112, 88, 72);
            case "STEEL": return new Color(184, 184, 208);
            case "FAIRY": return new Color(240, 182, 188);
            default: return new Color(168, 168, 120); // Normal
        }
    }

    /**
     * Añade o quita un movimiento de la selección y actualiza el botón.
     *
     * @param move   el movimiento seleccionado o deseleccionado
     * @param button el botón asociado al movimiento
     */
    private void toggleMoveSelection(Move move, JButton button) {
        if (selectedMoves.contains(move)) {
            selectedMoves.remove(move);
            button.setBorder(createMoveBorder(false));
            button.setBackground(getMoveTypeColor(move.type()));
        } else if (selectedMoves.size() < 4) {
            selectedMoves.add(move);
            button.setBorder(createMoveBorder(true));
            button.setBackground(getMoveTypeColor(move.type()).darker());
        } else {
            // Mostrar mensaje cuando ya se han seleccionado 4 movimientos
            JOptionPane.showMessageDialog(this,
                    "Ya has seleccionado 4 movimientos. Deselecciona uno primero.",
                    "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
        }

        updateConfirmButton();
    }

    /**
     * Actualiza el botón de confirmación con el número actual de movimientos seleccionados.
     */
    private void updateConfirmButton() {
        confirmButton.setText("Confirmar (" + selectedMoves.size() + "/4)");
        confirmButton.setEnabled(selectedMoves.size() == 4);

        // Cambiar color según el estado
        if (selectedMoves.size() == 4) {
            confirmButton.setBackground(EMERALD_GREEN);
        } else {
            confirmButton.setBackground(new Color(120, 120, 120));
        }
    }

    /**
     * Valida y asigna los movimientos seleccionados al Pokémon.
     * Muestra un error si no hay exactamente 4 movimientos seleccionados.
     */
    private void confirmSelection() {
        if (selectedMoves.size() != 4) {
            JOptionPane.showMessageDialog(this,
                    "Debes seleccionar exactamente 4 movimientos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Move> clonedMoves = new ArrayList<>();
        for (Move move : selectedMoves) {
            clonedMoves.add(move.clone());
        }

        pokemon.setMoves(clonedMoves);

        // Mostrar mensaje de confirmación
        JOptionPane.showMessageDialog(this,
                "¡Movimientos asignados exitosamente a " + pokemon.getName() + "!",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    /**
     * Muestra el diálogo para seleccionar movimientos.
     *
     * @param parent  el componente padre
     * @param pokemon el Pokémon que recibirá los movimientos seleccionados
     */
    public static void showMoveSelection(Component parent, Pokemon pokemon) {
        MoveSelectionGUI dialog = new MoveSelectionGUI(
                parent instanceof JFrame ? (JFrame) parent : null,
                pokemon
        );
        dialog.setVisible(true);
    }
}