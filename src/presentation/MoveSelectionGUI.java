package presentation;

import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setupUI();
    }

    /**
     * Configura la interfaz gráfica del diálogo.
     * Establece paneles, etiquetas y botones.
     */
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        pokemonInfoLabel = new JLabel(getPokemonInfoText(), JLabel.CENTER);
        pokemonInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(pokemonInfoLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        movesPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        movesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(movesPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        confirmButton = new JButton("Confirmar (" + selectedMoves.size() + "/4)");
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> confirmSelection());

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> dispose());

        bottomPanel.add(cancelButton);
        bottomPanel.add(confirmButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadAvailableMoves();
    }

    /**
     * Retorna el texto HTML con la información del Pokémon.
     *
     * @return cadena de texto con nombre, nivel y tipo del Pokémon
     */
    private String getPokemonInfoText() {
        return "<html><b>" + pokemon.getName() + "</b> (Nv. " + pokemon.getLevel() +
                ") - Tipo: " + pokemon.getType() + "</html>";
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
        JButton button = new JButton("<html><b>" + move.name() + "</b><br>" +
                "Tipo: " + move.type() + "<br>" +
                "Poder: " + move.power() + " | PP: " + move.maxPP() + "</html>");

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(getMoveTypeColor(move.type()));
        button.setForeground(Color.WHITE);
        button.addActionListener(e -> toggleMoveSelection(move, button));

        return button;
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
            button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        } else if (selectedMoves.size() < 4) {
            selectedMoves.add(move);
            button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
        }

        updateConfirmButton();
    }

    /**
     * Actualiza el botón de confirmación con el número actual de movimientos seleccionados.
     */
    private void updateConfirmButton() {
        confirmButton.setText("Confirmar (" + selectedMoves.size() + "/4)");
        confirmButton.setEnabled(selectedMoves.size() == 4);
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
