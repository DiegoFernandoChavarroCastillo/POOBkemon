package presentation;

import domain.*;
import domain.Action;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;



/**
 * The BattleGUI class represents the graphical user interface for the Pokémon battle game.
 * It handles the display of the battle scene, player interactions, and game flow.
 */
public class BattleGUI extends JFrame {
    private JPanel panelSuperior, panelInferior, panelPok1, panelPok2, panelImagenes;
    private JLabel labelInfo1, labelInfo2, infoLabel;
    private JProgressBar hpBar1, hpBar2;
    private JButton btnAtacar, btnCambiar, btnItem, btnExtra;
    private JLabel pok1Label, pok2Label;
    private Battle battle;
    private JPanel panelOpciones;
    private CardLayout cardLayout;
    private GameController controller;

    /**
     * Constructs a new BattleGUI instance and initializes the main menu.
     */
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

        pvpButton.addActionListener(e -> showPlayerSetup(1));
        pvmButton.addActionListener(e -> showPlayerSetup(2));
        mvmButton.addActionListener(e -> showPlayerSetup(3));

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

    private void setupBattleWindow() {
        getContentPane().removeAll();
        setTitle("POOBkemon Battle");
        setSize(700, 600);
        prepareElements();
        prepareActions();
        prepareListeners();
        updateBattleInfo();
        revalidate();
        repaint();
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
        setupBattleWindow();
    }

    public void updateBattleInfo() {
        if (battle == null) {
            System.err.println("Error: Battle is null");
            showDefaultBattleInfo();
            return;
        }


        Trainer player1 = battle.getPlayer1();
        Trainer player2 = battle.getPlayer2();

        if (player1 == null || player2 == null) {
            System.err.println("Error: One or both trainers are null");
            showDefaultBattleInfo();
            return;
        }
        if (!battle.isFinished()) {
            Trainer current = battle.getCurrentPlayer();
            Pokemon active = current.getActivePokemon();

            if (active != null && active.getHp() <= 0 && !current.isCPU()) {
                handleFaintedPokemon(current);
            }
        }


        Pokemon pok1 = player1.getActivePokemon();
        Pokemon pok2 = player2.getActivePokemon();

        if (pok1 == null || pok2 == null) {
            System.err.println("Error: One or both active Pokémon are null");
            showDefaultBattleInfo();
            return;
        }


        updatePokemonInfo(pok1, labelInfo1, hpBar1, panelPok1);


        updatePokemonInfo(pok2, labelInfo2, hpBar2, panelPok2);


        loadPokemonSprite(pok1Label, pok1.getName().toLowerCase());
        loadPokemonSprite(pok2Label, pok2.getName().toLowerCase());


        String turnInfo;
        Color turnColor;
        if (battle.getCurrentPlayer() == player1) {
            turnInfo = "Turno de " + player1.getName();
            turnColor = new Color(50, 150, 250); // Blue for player 1
            panelPok1.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
            panelPok2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        } else {
            turnInfo = "Turno de " + player2.getName();
            turnColor = new Color(250, 50, 50); // Red for player 2/CPU
            panelPok1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            panelPok2.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        }


        String statusText = "<html><div style='text-align:center;'>" +
                "<b><font color='" + String.format("#%02x%02x%02x",
                turnColor.getRed(), turnColor.getGreen(), turnColor.getBlue()) + "'>" +
                turnInfo + "</font></b><br>" +
                player1.getName() + ": " +
                "<b>" + pok1.getName() + "</b> (" + pok1.getHp() + "/" + pok1.getMaxHp() + " HP)<br>" +
                player2.getName() + ": " +
                "<b>" + pok2.getName() + "</b> (" + pok2.getHp() + "/" + pok2.getMaxHp() + " HP)";


        if (Battle.getClimate() != null) {
            statusText += "<br>Clima: <i>" + Battle.getClimate() + "</i>";
        }

        statusText += "</div></html>";

        infoLabel.setText(statusText);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Update button states based on turn
        boolean isHumanTurn = !battle.getCurrentPlayer().isCPU();
        btnAtacar.setEnabled(isHumanTurn);
        btnCambiar.setEnabled(isHumanTurn);
        btnItem.setEnabled(isHumanTurn);
        btnExtra.setEnabled(isHumanTurn);


        if (!battle.isFinished()) {
            Trainer current = battle.getCurrentPlayer();
            Pokemon active = current.getActivePokemon();

            // If human player's Pokémon fainted and they have others
            if (active != null && active.getHp() <= 0 && !current.isCPU()) {
                if (!current.getTeam().isAllFainted()) {
                    showSwitchPokemonDialog(current);
                }
            }
        }
    }

    private void updatePokemonInfo(Pokemon pokemon, JLabel infoLabel, JProgressBar hpBar, JPanel panel) {
        infoLabel.setText(pokemon.getName() + " Lv." + pokemon.getLevel());
        hpBar.setMaximum(pokemon.getMaxHp());
        hpBar.setValue(Math.max(0, pokemon.getHp())); // Ensure HP doesn't show negative
        hpBar.setString(pokemon.getHp() + "/" + pokemon.getMaxHp());
        hpBar.setStringPainted(true);

        // Update HP bar color based on percentage
        double hpPercentage = (double) pokemon.getHp() / pokemon.getMaxHp();
        if (hpPercentage > 0.5) {
            hpBar.setForeground(Color.GREEN);
        } else if (hpPercentage > 0.2) {
            hpBar.setForeground(Color.YELLOW);
        } else {
            hpBar.setForeground(Color.RED);
        }


        if (pokemon.getHp() <= 0) {
            panel.setBackground(new Color(255, 200, 200)); // Light red for fainted
            hpBar.setForeground(Color.GRAY);
            infoLabel.setForeground(Color.GRAY);
        } else {
            panel.setBackground(new Color(255, 255, 153)); // Light yellow for normal
            infoLabel.setForeground(Color.BLACK);
        }
    }

    private void showDefaultBattleInfo() {

        labelInfo1.setText("No Pokémon");
        hpBar1.setMaximum(100);
        hpBar1.setValue(0);
        hpBar1.setString("0/0");
        hpBar1.setForeground(Color.RED);

        labelInfo2.setText("No Pokémon");
        hpBar2.setMaximum(100);
        hpBar2.setValue(0);
        hpBar2.setString("0/0");
        hpBar2.setForeground(Color.RED);

        infoLabel.setText("<html><div style='text-align:center;'>" +
                "<b>Error loading battle information</b><br>" +
                "Please check the console for details</div></html>");


        pok1Label.setIcon(null);
        pok1Label.setText("No Pokémon");
        pok2Label.setIcon(null);
        pok2Label.setText("No Pokémon");


        btnAtacar.setEnabled(false);
        btnCambiar.setEnabled(false);
        btnItem.setEnabled(false);
        btnExtra.setEnabled(false);
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
                    BufferedImage scaledImage = new BufferedImage(
                            spriteWidth, spriteHeight, BufferedImage.TYPE_INT_ARGB);

                    Graphics2D g2 = scaledImage.createGraphics();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.drawImage(originalImage, 0, 0, spriteWidth, spriteHeight, null);
                    g2.dispose();

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

    private void updateHpBarColor(JProgressBar bar, int currentHp, int maxHp) {
        double percentage = (double) currentHp / maxHp;
        if (percentage > 0.5) {
            bar.setForeground(Color.GREEN);
        } else if (percentage > 0.2) {
            bar.setForeground(Color.YELLOW);
        } else {
            bar.setForeground(Color.RED);
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

    private void prepareActions() {

    }

    private void prepareListeners() {
        btnAtacar.addActionListener(e -> showAttackOptions());

        btnCambiar.addActionListener(e -> {
            Trainer current = battle.getCurrentPlayer();
            String[] pokemons = new String[current.getTeam().getPokemons().size()];

            for (int i = 0; i < pokemons.length; i++) {
                Pokemon p = current.getTeam().getPokemons().get(i);
                String status = (p.getHp() <= 0) ? " - Debilitado" : "";
                pokemons[i] = p.getName() + " (HP: " + p.getHp() + "/" + p.getMaxHp() + ")" + status;
            }

            String selected = (String) JOptionPane.showInputDialog(
                    this,
                    "Selecciona un Pokémon:",
                    "Cambiar Pokémon",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    pokemons,
                    pokemons[0]);

            if (selected != null) {
                int pokemonIndex = Arrays.asList(pokemons).indexOf(selected);
                controller.getCurrentBattle().performAction(Action.createSwitchPokemon(pokemonIndex));
                updateBattleInfo();
                endPlayerTurn();
            }
        });

        btnItem.addActionListener(e -> {
            Trainer current = battle.getCurrentPlayer();

            if (current.getItems().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No tienes ítems disponibles.");
                return;
            }

            String[] itemNames = current.getItems().stream()
                    .map(Item::getName)
                    .toArray(String[]::new);

            String selectedItemName = (String) JOptionPane.showInputDialog(
                    this,
                    "Selecciona un ítem:",
                    "Usar Ítem",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    itemNames,
                    itemNames[0]);

            if (selectedItemName != null) {
                int itemIndex = Arrays.asList(itemNames).indexOf(selectedItemName);

                String[] targets = new String[current.getTeam().getPokemons().size()];
                for (int i = 0; i < targets.length; i++) {
                    Pokemon p = current.getTeam().getPokemons().get(i);
                    targets[i] = p.getName() + " (HP: " + p.getHp() + "/" + p.getMaxHp() + ")";
                }

                String selectedTarget = (String) JOptionPane.showInputDialog(
                        this,
                        "Selecciona un Pokémon objetivo:",
                        "Objetivo del Ítem",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        targets,
                        targets[0]);

                if (selectedTarget != null) {
                    int targetIndex = Arrays.asList(targets).indexOf(selectedTarget);
                    battle.performAction(Action.createUseItem(itemIndex, targetIndex));
                    updateBattleInfo();
                    endPlayerTurn();
                }
            }
        });

        btnExtra.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "¿Quieres rendirte?",
                    "Rendición",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Te has rendido. ¡Perdiste la batalla!");
                System.exit(0);
            }
        });
    }

    private void showAttackOptions() {
        Pokemon active = battle.getCurrentPlayer().getActivePokemon();
        List<Move> moves = active.getMoves();

        JPanel attackPanel = (JPanel) panelOpciones.getComponent(1);
        attackPanel.removeAll();

        for (Move move : moves) {
            JButton moveButton = new JButton(move.name() + " (PP: " + move.pp() + "/" + move.maxPP() + ")");
            moveButton.setBackground(Color.ORANGE);
            moveButton.addActionListener(e -> {
                int moveIndex = moves.indexOf(move);
                controller.getCurrentBattle().performAction(Action.createAttack(moveIndex));
                updateBattleInfo();

                if (!battle.isFinished()) {
                    cardLayout.show(panelOpciones, "main");
                    endPlayerTurn();
                } else {
                    checkBattleEnd();
                }
            });

            if (move.pp() <= 0) {
                moveButton.setEnabled(false);
                moveButton.setBackground(Color.GRAY);
            }

            attackPanel.add(moveButton);
        }

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.addActionListener(e -> cardLayout.show(panelOpciones, "main"));
        attackPanel.add(cancelButton);

        cardLayout.show(panelOpciones, "attacks");
        attackPanel.revalidate();
        attackPanel.repaint();
    }

    private void checkBattleEnd() {
        if (battle.isFinished()) {
            Trainer winner = battle.getWinner();
            String message = winner != null ?
                    "¡" + winner.getName() + " ha ganado la batalla!" :
                    "¡La batalla ha terminado en empate!";

            JOptionPane.showMessageDialog(this, message);
            System.exit(0);
        }
    }

    private void endPlayerTurn() {
        if (battle.isFinished()) {
            checkBattleEnd();
            return;
        }


        battle.changeTurn();
        updateBattleInfo();


        if (!battle.isFinished() && battle.getCurrentPlayer().isCPU()) {
            executeCpuTurn();
        }
    }

    private void executeAutoBattleTurn() {
        Timer timer = new Timer(1500, e -> {
            if (!battle.isFinished()) {
                controller.getCurrentBattle().executeCpuTurn();
                updateBattleInfo();

                if (battle.getCurrentPlayer().isCPU()) {
                    executeAutoBattleTurn();
                }
            } else {
                checkBattleEnd();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showPlayerSetup(int gameMode) {
        JPanel setupPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        setupPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField player1Field = new JTextField(20);
        JTextField player2Field = new JTextField(20);

        if (gameMode != 3) {
            setupPanel.add(new JLabel("Nombre del Jugador 1:"));
            setupPanel.add(player1Field);
        }

        if (gameMode == 1) {
            setupPanel.add(new JLabel("Nombre del Jugador 2:"));
            setupPanel.add(player2Field);
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                setupPanel,
                "Configuración de jugadores",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String player1Name = gameMode == 3 ? "CPU Ash" :
                    (player1Field.getText().trim().isEmpty() ? "Jugador 1" : player1Field.getText());
            String player2Name = gameMode == 3 ? "CPU Gary" :
                    (player2Field.getText().trim().isEmpty() ? "Jugador 2" : player2Field.getText());

            this.controller = new GameController();
            this.controller.setGUI(this);
            this.controller.startGame(gameMode, player1Name, player2Name);
        }
    }

    private void executeCpuTurn() {
        Timer timer = new Timer(1000, e -> {
            controller.getCurrentBattle().executeCpuTurn();
            updateBattleInfo();

            if (!battle.isFinished()) {

                battle.changeTurn();
                updateBattleInfo();
            } else {
                checkBattleEnd();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showSwitchPokemonDialog(Trainer trainer) {
        String[] options = new String[trainer.getTeam().getPokemons().size() + 1];


        for (int i = 0; i < trainer.getTeam().getPokemons().size(); i++) {
            Pokemon p = trainer.getTeam().getPokemons().get(i);
            String status = (p.getHp() <= 0) ? " - Debilitado" : "";
            options[i] = p.getName() + " (HP: " + p.getHp() + "/" + p.getMaxHp() + ")" + status;
        }


        boolean hasRevive = trainer.getItems().stream()
                .anyMatch(item -> item instanceof Revive);

        if (hasRevive) {
            options[options.length - 1] = "Usar Revive";
        }

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "¡Tu Pokémon se ha debilitado! Elige acción:",
                "Cambiar Pokémon/Usar Revive",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (selected != null) {
            if (selected.equals("Usar Revive")) {
                Optional<Item> revive = trainer.getItems().stream()
                        .filter(item -> item instanceof Revive)
                        .findFirst();

                if (revive.isPresent()) {
                    revive.get().use(trainer.getActivePokemon());
                    trainer.getItems().remove(revive.get());
                    updateBattleInfo();
                }
            } else {
                int pokemonIndex = Arrays.asList(options).indexOf(selected);
                battle.performAction(Action.createSwitchPokemon(pokemonIndex));
                updateBattleInfo();
            }
        }
    }
    private void handleFaintedPokemon(Trainer trainer) {
        if (trainer.getTeam().isAllFainted()) {
            Optional<Item> revive = trainer.getItems().stream()
                    .filter(item -> item instanceof Revive)
                    .findFirst();

            if (revive.isPresent()) {
                revive.get().use(trainer.getActivePokemon());
                trainer.getItems().remove(revive.get());
                JOptionPane.showMessageDialog(this,
                        "¡Se usó un Revive automáticamente en " +
                                trainer.getActivePokemon().getName() + "!");
                updateBattleInfo();
            } else {
                checkBattleEnd();
            }
        } else {
            showSwitchPokemonDialog(trainer);
        }
    }

    /**
     * The main method to launch the application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(BattleGUI::new);
    }
}