package presentation.components;

import domain.Pokemon;
import javax.swing.*;
import java.awt.*;

/**
 * Panel que muestra la información de un Pokémon, incluyendo su nombre,
 * nivel y barra de salud. La barra de salud cambia de color según el
 * porcentaje de salud restante y el panel se actualiza visualmente
 * cuando el Pokémon está debilitado.
 */
public class PokemonInfoPanel extends JPanel {
    private JLabel nameLabel;
    private JProgressBar hpBar;
    private Font pokemonFont;

    /**
     * Constructor que inicializa el panel con la fuente especificada.
     *
     * @param pokemonFont La fuente personalizada para los textos del panel
     */
    public PokemonInfoPanel(Font pokemonFont) {
        this.pokemonFont = pokemonFont;
        initializeComponents();
    }

    /**
     * Inicializa los componentes del panel, configurando su diseño,
     * color de fondo y añadiendo los elementos visuales.
     */
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 200));
        setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        nameLabel = new JLabel("", JLabel.CENTER);
        nameLabel.setFont(pokemonFont.deriveFont(Font.BOLD, 14));

        hpBar = new JProgressBar(0, 100);
        hpBar.setValue(100);
        hpBar.setForeground(new Color(0, 200, 0));
        hpBar.setStringPainted(true);
        hpBar.setFont(pokemonFont.deriveFont(12f));
        hpBar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        add(nameLabel, BorderLayout.NORTH);
        add(hpBar, BorderLayout.SOUTH);
    }

    /**
     * Actualiza la información mostrada en el panel según el Pokémon proporcionado.
     *
     * @param pokemon El Pokémon cuyos datos se mostrarán en el panel
     */
    public void updatePokemonInfo(Pokemon pokemon) {
        nameLabel.setText(pokemon.getName() + " Lv." + pokemon.getLevel());

        hpBar.setMaximum(pokemon.getMaxHp());
        hpBar.setValue(Math.max(0, pokemon.getHp()));
        hpBar.setString(pokemon.getHp() + "/" + pokemon.getMaxHp());

        updateHealthBarColor(pokemon);
        updatePanelState(pokemon);
    }

    /**
     * Actualiza el color de la barra de salud según el porcentaje de salud restante.
     * Verde (>50%), Amarillo (>20%) o Rojo (≤20%).
     *
     * @param pokemon El Pokémon cuya barra de salud se actualizará
     */
    private void updateHealthBarColor(Pokemon pokemon) {
        double hpPercentage = (double) pokemon.getHp() / pokemon.getMaxHp();
        if (hpPercentage > 0.5) {
            hpBar.setForeground(new Color(0, 200, 0));
        } else if (hpPercentage > 0.2) {
            hpBar.setForeground(new Color(255, 200, 0));
        } else {
            hpBar.setForeground(new Color(200, 0, 0));
        }
    }

    /**
     * Actualiza el estado visual del panel cuando el Pokémon está debilitado,
     * cambiando los colores a tonos grises.
     *
     * @param pokemon El Pokémon cuyo estado se evaluará
     */
    private void updatePanelState(Pokemon pokemon) {
        if (pokemon.getHp() <= 0) {
            setBackground(new Color(200, 200, 200));
            hpBar.setForeground(Color.GRAY);
            nameLabel.setForeground(Color.GRAY);
        } else {
            setBackground(new Color(255, 255, 200));
            nameLabel.setForeground(Color.BLACK);
        }
    }

    /**
     * Resalta visualmente el panel para indicar que es el turno del Pokémon.
     *
     * @param isActive true para resaltar el panel, false para volver al estado normal
     */
    public void setTurnActive(boolean isActive) {
        if (isActive) {
            setBorder(BorderFactory.createLineBorder(new Color(200, 0, 0), 3));
        } else {
            setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        }
    }
}