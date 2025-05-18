package presentation;

import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Diálogo para seleccionar Pokémon que formarán parte del equipo de un entrenador.
 * Permite seleccionar hasta un número máximo especificado de Pokémon desde la base de datos disponible.
 */
public class PokemonSelectionGUI extends JDialog {
    private List<Pokemon> selectedPokemons;
    private Trainer trainer;
    private JPanel pokemonGrid;
    private JButton confirmButton;
    private int maxPokemons;
    private MoveSelectionCallback moveSelectionCallback;

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

        setupUI();
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Configura los componentes de la interfaz gráfica del diálogo.
     * Incluye una grilla con todos los Pokémon disponibles y el botón de confirmación.
     */
    private void setupUI() {
        setLayout(new BorderLayout());

        pokemonGrid = new JPanel(new GridLayout(0, 4, 10, 10));
        pokemonGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
            if (!selectedPokemons.isEmpty()) {
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

    /**
     * Crea el panel visual para un Pokémon, con su imagen y nombre.
     * Permite seleccionar o deseleccionar Pokémon con clic izquierdo/derecho.
     *
     * @param pokemon el Pokémon a representar gráficamente
     * @return un panel listo para ser agregado a la grilla
     */
    private JPanel createPokemonPanel(Pokemon pokemon) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

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

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (selectedPokemons.size() < maxPokemons) {
                        Pokemon clonedPokemon = pokemon.clone();
                        selectedPokemons.add(clonedPokemon);
                        panel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
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

                confirmButton.setText("Confirmar (" + selectedPokemons.size() + "/" + maxPokemons + ")");
                confirmButton.setEnabled(!selectedPokemons.isEmpty());
            }
        });

        return panel;
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
