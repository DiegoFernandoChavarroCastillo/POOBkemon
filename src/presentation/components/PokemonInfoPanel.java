package presentation.components;

import domain.Pokemon;
import javax.swing.*;
import java.awt.*;

public class PokemonInfoPanel extends JPanel {
    private JLabel nameLabel;
    private JProgressBar hpBar;
    private Font pokemonFont;

    public PokemonInfoPanel(Font pokemonFont) {
        this.pokemonFont = pokemonFont;
        initializeComponents();
    }

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

    public void updatePokemonInfo(Pokemon pokemon) {
        nameLabel.setText(pokemon.getName() + " Lv." + pokemon.getLevel());

        hpBar.setMaximum(pokemon.getMaxHp());
        hpBar.setValue(Math.max(0, pokemon.getHp()));
        hpBar.setString(pokemon.getHp() + "/" + pokemon.getMaxHp());

        updateHealthBarColor(pokemon);
        updatePanelState(pokemon);
    }

    private void updateHealthBarColor(Pokemon pokemon) {
        double hpPercentage = (double) pokemon.getHp() / pokemon.getMaxHp();
        if (hpPercentage > 0.5) {
            hpBar.setForeground(new Color(0, 200, 0)); // Verde
        } else if (hpPercentage > 0.2) {
            hpBar.setForeground(new Color(255, 200, 0)); // Amarillo
        } else {
            hpBar.setForeground(new Color(200, 0, 0)); // Rojo
        }
    }

    private void updatePanelState(Pokemon pokemon) {
        if (pokemon.getHp() <= 0) {
            setBackground(new Color(200, 200, 200)); // Gris cuando está debilitado
            hpBar.setForeground(Color.GRAY);
            nameLabel.setForeground(Color.GRAY);
        } else {
            setBackground(new Color(255, 255, 200)); // Amarillo claro
            nameLabel.setForeground(Color.BLACK);
        }
    }

    public void setTurnActive(boolean isActive) {
        if (isActive) {
            setBorder(BorderFactory.createLineBorder(new Color(200, 0, 0), 3));
        } else {
            setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        }
    }
}