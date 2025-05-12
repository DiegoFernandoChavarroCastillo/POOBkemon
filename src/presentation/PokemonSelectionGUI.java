package presentation;

import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A dialog for selecting Pokémon for a trainer's team. Allows selection of up to
 * a specified maximum number of Pokémon from available options.
 */
public class PokemonSelectionGUI extends JDialog {
    private List<Pokemon> selectedPokemons;
    private Trainer trainer;
    private JPanel pokemonGrid;
    private JButton confirmButton;
    private int maxPokemons;
    private MoveSelectionCallback moveSelectionCallback;

    /**
     * Constructs a PokemonSelectionGUI dialog.
     *
     * @param parent              the parent frame of this dialog
     * @param trainer             the trainer who will receive the selected Pokémon
     * @param maxPokemons         the maximum number of Pokémon that can be selected
     * @param callback            callback to handle Pokémon selection confirmation
     */
    public PokemonSelectionGUI(JFrame parent, Trainer trainer, int maxPokemons, MoveSelectionCallback callback) {
        super(parent, "Seleccionar Pokémon", true);
        this.trainer = trainer;
        this.selectedPokemons = new ArrayList<>();
        this.maxPokemons = maxPokemons;
        this.moveSelectionCallback = callback;

        setupUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void setupUI() {
        setLayout(new BorderLayout());


        pokemonGrid = new JPanel(new GridLayout(0, 4, 10, 10));
        pokemonGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Cargar todos los Pokémon disponibles
        for (String name : PokemonDataBase.getAvailablePokemonNames()) {
            Pokemon pokemon = PokemonDataBase.getPokemon(name);
            JPanel pokemonPanel = createPokemonPanel(pokemon);
            pokemonGrid.add(pokemonPanel);
        }

        JScrollPane scrollPane = new JScrollPane(pokemonGrid);
        add(scrollPane, BorderLayout.CENTER);


        JPanel bottomPanel = new JPanel();
        confirmButton = new JButton("Confirmar (" + selectedPokemons.size() + "/" + maxPokemons + ")");
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> {
            if (selectedPokemons.size() > 0) {
                // Usar el método correcto para añadir Pokémon al equipo
                for (Pokemon pokemon : selectedPokemons) {
                    trainer.addPokemonToTeam(pokemon);
                }
                dispose();
                moveSelectionCallback.onPokemonSelected(new ArrayList<>(selectedPokemons));
            }
        });
        bottomPanel.add(confirmButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createPokemonPanel(Pokemon pokemon) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Cargar sprite
        try {
            ImageIcon icon = new ImageIcon("src/sprites/" + pokemon.getName().toLowerCase() + ".png");
            Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            panel.add(imageLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            panel.add(new JLabel(pokemon.getName(), JLabel.CENTER), BorderLayout.CENTER);
        }

        JLabel nameLabel = new JLabel(pokemon.getName(), JLabel.CENTER);
        panel.add(nameLabel, BorderLayout.SOUTH);

        // Manejar clic izquierdo (seleccionar) y derecho (deseleccionar)
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // Clic izquierdo: seleccionar (si hay espacio)
                    if (selectedPokemons.size() < maxPokemons) {
                        Pokemon clonedPokemon = pokemon.clone();
                        selectedPokemons.add(clonedPokemon);
                        panel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // Clic derecho: deseleccionar (si existe en la lista)
                    Pokemon toRemove = null;
                    for (Pokemon p : selectedPokemons) {
                        if (p.getName().equals(pokemon.getName())) {
                            toRemove = p;
                            break;
                        }
                    }
                    if (toRemove != null) {
                        selectedPokemons.remove(toRemove);
                        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    }
                }

                // Actualizar botón de confirmación
                confirmButton.setText("Confirmar (" + selectedPokemons.size() + "/" + maxPokemons + ")");
                confirmButton.setEnabled(selectedPokemons.size() > 0);
            }
        });

        return panel;
    }

    public interface MoveSelectionCallback {
        void onPokemonSelected(List<Pokemon> pokemons);
    }
}