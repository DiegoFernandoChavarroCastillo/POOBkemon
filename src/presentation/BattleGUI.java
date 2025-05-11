package presentation;

import domain.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


/**
 * The BattleGUI class represents the graphical user interface for the Pokémon battle game.
 * It handles ONLY the display of the battle scene and user interactions.
 */
public class BattleGUI extends JFrame {
    private JPanel panelSuperior, panelInferior, panelPok1, panelPok2, panelImagenes;
    private JLabel labelInfo1, labelInfo2, infoLabel;
    private JProgressBar hpBar1, hpBar2;
    private JButton btnAtacar, btnCambiar, btnItem, btnExtra;
    private JLabel pok1Label, pok2Label;
    private JPanel panelOpciones;
    private CardLayout cardLayout;
    private GameController controller;

    public BattleGUI() {
        setTitle("POOBkemon Battle - Menú Principal");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        prepareMenu();
        setVisible(true);
    }

    private void prepareMenu() {
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        try {
            BufferedImage logoImage = ImageIO.read(new File("src/sprites/Logo.png"));
            Image scaledLogo = logoImage.getScaledInstance(200, 100, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            logoPanel.add(logoLabel);
            logoPanel.setBackground(new Color(194, 255, 82));
        } catch (IOException e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
            JLabel titleLabel = new JLabel("¡Bienvenido a POOBkemon Battle!", JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            logoPanel.add(titleLabel);
        }

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton pvpButton = new JButton("Jugador vs Jugador (PvP)");
        JButton pvmButton = new JButton("Jugador vs Máquina (PvM)");
        JButton mvmButton = new JButton("Máquina vs Máquina (MvM)");

        pvpButton.addActionListener(e -> controller.showPlayerSetup(1));
        pvmButton.addActionListener(e -> controller.showPlayerSetup(2));
        mvmButton.addActionListener(e -> controller.showPlayerSetup(3));

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        pvpButton.setFont(buttonFont);
        pvmButton.setFont(buttonFont);
        mvmButton.setFont(buttonFont);

        pvpButton.setBackground(new Color(100, 150, 255));
        pvmButton.setBackground(new Color(100, 200, 100));
        mvmButton.setBackground(new Color(255, 150, 100));

        pvpButton.setForeground(Color.WHITE);
        pvmButton.setForeground(Color.WHITE);
        mvmButton.setForeground(Color.WHITE);

        buttonPanel.add(pvpButton);
        buttonPanel.add(pvmButton);
        buttonPanel.add(mvmButton);

        menuPanel.add(logoPanel, BorderLayout.NORTH);
        menuPanel.add(buttonPanel, BorderLayout.CENTER);
        menuPanel.setBackground(new Color(194, 255, 82));

        add(menuPanel);
    }

    public void setupBattleWindow() {
        getContentPane().removeAll();
        setTitle("POOBkemon Battle");
        setSize(700, 600);
        prepareElements();
        prepareListeners();
        revalidate();
        repaint();
    }

    public void updateBattleInfo(BattleState state) {
        updatePokemonInfo(state.getPlayer1Pokemon(), labelInfo1, hpBar1, panelPok1);
        updatePokemonInfo(state.getPlayer2Pokemon(), labelInfo2, hpBar2, panelPok2);

        loadPokemonSprite(pok1Label, state.getPlayer1Pokemon().getName().toLowerCase());
        loadPokemonSprite(pok2Label, state.getPlayer2Pokemon().getName().toLowerCase());

        String turnInfo = "Turno de " + state.getCurrentPlayerName();
        Color turnColor = state.isPlayer1Turn() ? new Color(50, 150, 250) : new Color(250, 50, 50);

        panelPok1.setBorder(BorderFactory.createLineBorder(state.isPlayer1Turn() ? Color.RED : Color.BLACK,
                state.isPlayer1Turn() ? 3 : 2));
        panelPok2.setBorder(BorderFactory.createLineBorder(state.isPlayer1Turn() ? Color.BLACK : Color.RED,
                state.isPlayer1Turn() ? 2 : 3));

        String statusText = "<html><div style='text-align:center;'>" +
                "<b><font color='" + String.format("#%02x%02x%02x",
                turnColor.getRed(), turnColor.getGreen(), turnColor.getBlue()) + "'>" +
                turnInfo + "</font></b><br>" +
                state.getPlayer1Name() + ": " +
                "<b>" + state.getPlayer1Pokemon().getName() + "</b> (" + state.getPlayer1Pokemon().getHp() + "/" +
                state.getPlayer1Pokemon().getMaxHp() + " HP)<br>" +
                state.getPlayer2Name() + ": " +
                "<b>" + state.getPlayer2Pokemon().getName() + "</b> (" + state.getPlayer2Pokemon().getHp() + "/" +
                state.getPlayer2Pokemon().getMaxHp() + " HP)";

        if (state.getClimate() != null) {
            statusText += "<br>Clima: <i>" + state.getClimate() + "</i>";
        }

        statusText += "</div></html>";
        infoLabel.setText(statusText);

        boolean isHumanTurn = state.isHumanTurn();
        btnAtacar.setEnabled(isHumanTurn);
        btnCambiar.setEnabled(isHumanTurn);
        btnItem.setEnabled(isHumanTurn);
        btnExtra.setEnabled(isHumanTurn);
    }

    private void updatePokemonInfo(Pokemon pokemon, JLabel infoLabel, JProgressBar hpBar, JPanel panel) {
        infoLabel.setText(pokemon.getName() + " Lv." + pokemon.getLevel());
        hpBar.setMaximum(pokemon.getMaxHp());
        hpBar.setValue(Math.max(0, pokemon.getHp()));
        hpBar.setString(pokemon.getHp() + "/" + pokemon.getMaxHp());
        hpBar.setStringPainted(true);

        double hpPercentage = (double) pokemon.getHp() / pokemon.getMaxHp();
        if (hpPercentage > 0.5) {
            hpBar.setForeground(Color.GREEN);
        } else if (hpPercentage > 0.2) {
            hpBar.setForeground(Color.YELLOW);
        } else {
            hpBar.setForeground(Color.RED);
        }

        if (pokemon.getHp() <= 0) {
            panel.setBackground(new Color(255, 200, 200));
            hpBar.setForeground(Color.GRAY);
            infoLabel.setForeground(Color.GRAY);
        } else {
            panel.setBackground(new Color(255, 255, 153));
            infoLabel.setForeground(Color.BLACK);
        }
    }

    private void loadPokemonSprite(JLabel label, String pokemonName) {
        String basePath = "src/sprites/";
        int spriteWidth = 200;
        int spriteHeight = 200;

        try {
            for (String ext : new String[]{".png", ".jpg", ".gif"}) {
                File file = new File(basePath + pokemonName + ext);
                if (file.exists()) {
                    BufferedImage originalImage = ImageIO.read(file);
                    Image scaledImage = originalImage.getScaledInstance(spriteWidth, spriteHeight, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(scaledImage));
                    return;
                }
            }
            label.setIcon(null);
            label.setText(pokemonName);
        } catch (IOException e) {
            System.err.println("Error al cargar imagen: " + e.getMessage());
            label.setIcon(null);
            label.setText(pokemonName);
        }
    }

    private void prepareElements() {
        panelSuperior = new JPanel(new GridLayout(1, 2));
        panelSuperior.setBackground(new Color(194, 255, 82));
        panelSuperior.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        panelPok1 = new JPanel(new BorderLayout());
        panelPok1.setBackground(new Color(255, 255, 153));
        panelPok1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        labelInfo1 = new JLabel("pok1     level");
        hpBar1 = new JProgressBar(0, 100);
        hpBar1.setValue(80);
        hpBar1.setForeground(Color.GREEN);
        panelPok1.add(labelInfo1, BorderLayout.NORTH);
        panelPok1.add(hpBar1, BorderLayout.SOUTH);

        panelPok2 = new JPanel(new BorderLayout());
        panelPok2.setBackground(new Color(255, 255, 153));
        panelPok2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        labelInfo2 = new JLabel("pok2     level");
        hpBar2 = new JProgressBar(0, 100);
        hpBar2.setValue(60);
        hpBar2.setForeground(Color.GREEN);
        panelPok2.add(labelInfo2, BorderLayout.NORTH);
        panelPok2.add(hpBar2, BorderLayout.SOUTH);

        panelSuperior.add(panelPok1);
        panelSuperior.add(panelPok2);

        panelImagenes = new JPanel(new GridLayout(1, 2));
        panelImagenes.setBackground(new Color(194, 255, 82));
        pok1Label = new JLabel(new ImageIcon("src/sprites/pok1.png"), JLabel.CENTER);
        pok2Label = new JLabel(new ImageIcon("src/sprites/pok2.png"), JLabel.CENTER);
        panelImagenes.add(pok1Label);
        panelImagenes.add(pok2Label);

        cardLayout = new CardLayout();
        panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(Color.YELLOW);
        panelInferior.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        JPanel panelInfo = new JPanel();
        panelInfo.setPreferredSize(new Dimension(300, 100));
        panelInfo.setBackground(Color.decode("#FDF074"));
        panelInfo.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        infoLabel = new JLabel("Panel de información");
        panelInfo.add(infoLabel);

        JPanel mainOptionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        mainOptionsPanel.setBackground(Color.decode("#C8FC4B"));
        mainOptionsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        btnAtacar = new JButton("Atacar");
        btnAtacar.setBackground(Color.RED);
        btnAtacar.setForeground(Color.WHITE);

        btnCambiar = new JButton("Cambiar");
        btnCambiar.setBackground(Color.BLUE);
        btnCambiar.setForeground(Color.WHITE);

        btnItem = new JButton("Usar Ítem");
        btnItem.setBackground(Color.GREEN.darker());
        btnItem.setForeground(Color.WHITE);

        btnExtra = new JButton("Huir");
        btnExtra.setBackground(Color.LIGHT_GRAY);

        mainOptionsPanel.add(btnAtacar);
        mainOptionsPanel.add(btnCambiar);
        mainOptionsPanel.add(btnItem);
        mainOptionsPanel.add(btnExtra);

        JPanel attackOptionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        attackOptionsPanel.setBackground(Color.decode("#C8FC4B"));
        attackOptionsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        panelOpciones = new JPanel(cardLayout);
        panelOpciones.add(mainOptionsPanel, "main");
        panelOpciones.add(attackOptionsPanel, "attacks");

        panelInferior.add(panelInfo, BorderLayout.WEST);
        panelInferior.add(panelOpciones, BorderLayout.CENTER);

        add(panelSuperior, BorderLayout.NORTH);
        add(panelImagenes, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void prepareListeners() {
        btnAtacar.addActionListener(e -> controller.showAttackOptions());

        btnCambiar.addActionListener(e -> controller.showSwitchPokemonDialog());

        btnItem.addActionListener(e -> controller.showItemSelectionDialog());

        btnExtra.addActionListener(e -> controller.handleSurrender());
    }

    public void showAttackOptions(List<Move> moves) {
        JPanel attackPanel = (JPanel) panelOpciones.getComponent(1);
        attackPanel.removeAll();
        attackPanel.setLayout(new GridLayout(0, 1, 5, 5)); // Mejor distribución de los botones

        Map<JButton, Integer> buttonIndexMap = new HashMap<>();

        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            JButton moveButton = new JButton(move.name() + " (PP: " + move.pp() + "/" + move.maxPP() + ")");
            moveButton.setBackground(Color.ORANGE);
            moveButton.setFont(new Font("Arial", Font.BOLD, 12));

            final int moveIndex = i;
            moveButton.addActionListener(e -> controller.executeAttack(moveIndex));

            if (move.pp() <= 0) {
                moveButton.setEnabled(false);
                moveButton.setBackground(Color.GRAY);
            }

            attackPanel.add(moveButton);
            buttonIndexMap.put(moveButton, i);
        }

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.addActionListener(e -> cardLayout.show(panelOpciones, "main"));
        attackPanel.add(cancelButton);

        cardLayout.show(panelOpciones, "attacks");
        attackPanel.revalidate();
        attackPanel.repaint();
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void showBattleEnd(String message) {
        JOptionPane.showMessageDialog(this, message);
        System.exit(0);
    }
    public boolean isAttackPanelVisible() {
        return ((JPanel)panelOpciones.getComponent(1)).getComponentCount() > 0;
    }

    public void showMainOptions() {
        cardLayout.show(panelOpciones, "main");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BattleGUI gui = new BattleGUI();
            GameController controller = new GameController(gui);
            gui.setController(controller);
        });
    }
}