package presentation;

import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * A dialog for selecting moves for a Pokémon. Allows the user to choose exactly 4 moves
 * from the available moves in the MoveDatabase.
 */
public class MoveSelectionGUI extends JDialog {
    private Pokemon pokemon;
    private List<Move> selectedMoves;
    private JPanel movesPanel;
    private JButton confirmButton;
    private JLabel pokemonInfoLabel;

    /**
     * Constructs a MoveSelectionGUI dialog.
     *
     * @param parent  the parent frame of this dialog
     * @param pokemon the Pokémon that will receive the selected moves
     */
    public MoveSelectionGUI(JFrame parent, Pokemon pokemon) {
        super(parent, "Seleccionar Movimientos para " + pokemon.getName(), true);
        this.pokemon = pokemon;
        this.selectedMoves = new ArrayList<>();

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setupUI();
    }

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

    private String getPokemonInfoText() {
        return "<html><b>" + pokemon.getName() + "</b> (Nv. " + pokemon.getLevel() +
                ") - Tipo: " + pokemon.getType() + "</html>";
    }

    private void loadAvailableMoves() {
        List<Move> availableMoves = MoveDatabase.getAvailableMoves();


        availableMoves.sort(Comparator.comparing(Move::name));

        for (Move move : availableMoves) {
            JButton moveButton = createMoveButton(move);
            movesPanel.add(moveButton);
        }

        movesPanel.revalidate();
    }

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

    private void updateConfirmButton() {
        confirmButton.setText("Confirmar (" + selectedMoves.size() + "/4)");
        confirmButton.setEnabled(selectedMoves.size() == 4);
    }

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
     * Displays the move selection dialog.
     *
     * @param parent   the parent component
     * @param pokemon  the Pokémon that will receive the selected moves
     */
    public static void showMoveSelection(Component parent, Pokemon pokemon) {
        MoveSelectionGUI dialog = new MoveSelectionGUI(
                parent instanceof JFrame ? (JFrame) parent : null,
                pokemon
        );
        dialog.setVisible(true);
    }
}