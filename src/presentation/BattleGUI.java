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
 *
 * @author Diego Chavarro
 * @author Diego Rodriguez
 * @version 1.0
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
    private JLabel turnTimerLabel;
    private int gameMode;

    /**
     * Constructs a new BattleGUI instance which initializes the main menu window.
     * Sets default window properties and prepares the menu interface.
     */
    public BattleGUI() {
        setTitle("POOBkemon Battle - Menú Principal");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        prepareMenuBar(); // Nueva barra de menú
        prepareMenu();
        setVisible(true);
        controller = new GameController(this);
        setController(controller);
    }

    /**
     * Prepara la barra de menú con las opciones de Archivo (Guardar/Cargar)
     */
    private void prepareMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menú Archivo
        JMenu fileMenu = new JMenu("Archivo");

        JMenuItem saveItem = new JMenuItem("Guardar partida");
        saveItem.addActionListener(e -> saveGame());

        JMenuItem loadItem = new JMenuItem("Cargar partida");
        loadItem.addActionListener(e -> loadGame());

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Maneja el guardado de la partida actual
     */
    private void saveGame() {
        if (controller.getCurrentBattle() == null) {
            JOptionPane.showMessageDialog(this, "No hay partida en curso para guardar");
            return;
        }

        String filename = JOptionPane.showInputDialog(this, "Nombre para guardar la partida:");
        if (filename != null && !filename.trim().isEmpty()) {
            boolean success = PersistenceManager.saveGame(
                    new GameState(
                            controller.getCurrentBattle(),
                            gameMode,
                            controller.getCurrentBattle().getPlayer1().getName(),
                            controller.getCurrentBattle().getPlayer2().getName()
                    ),
                    filename
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Partida guardada exitosamente");
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar la partida", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Maneja la carga de una partida guardada
     */
    private void loadGame() {
        List<String> savedGames = PersistenceManager.getSavedGames();
        if (savedGames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay partidas guardadas");
            return;
        }

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Selecciona una partida para cargar:",
                "Cargar partida",
                JOptionPane.PLAIN_MESSAGE,
                null,
                savedGames.toArray(),
                savedGames.get(0));

        if (selected != null) {
            GameState gameState = PersistenceManager.loadGame(selected);
            if (gameState != null) {
                controller.loadGameState(gameState);
                this.gameMode = gameState.getGameMode();
                setupBattleWindow();
                updateBattleInfo(gameState.getBattle().getBattleState());
                JOptionPane.showMessageDialog(this, "Partida cargada exitosamente");
            }
        }
    }

    // Todos los demás métodos existentes se mantienen exactamente igual...

    /**
     * Prepares the main menu interface with game mode selection buttons.
     * Creates panels for logo, buttons, and information display.
     */
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

        JButton pvpButton = createModeButton("Jugador vs Jugador (PvP)",
                new Color(100, 150, 255),
                "Modo PvP - Jugador vs Jugador\n\n" +
                        "Ambos jugadores controlan sus Pokémon manualmente.\n" +
                        "Cada jugador seleccionará:\n" +
                        "- 6 Pokémon\n" +
                        "- Movimientos para cada Pokémon\n" +
                        "- Ítems para usar en batalla");

        JButton pvmButton = createModeButton("Jugador vs Máquina (PvM)",
                new Color(100, 200, 100),
                "Modo PvM - Jugador vs Máquina\n\n" +
                        "Tú controlas tu equipo y configuras el equipo de la CPU.\n" +
                        "Seleccionarás:\n" +
                        "- Tus 6 Pokémon y sus movimientos\n" +
                        "- Tus ítems\n" +
                        "- Estrategia de la CPU (Defensiva, Ofensiva, etc.)\n" +
                        "- Pokémon, movimientos e ítems de la CPU");

        JButton mvmButton = createModeButton("Máquina vs Máquina (MvM)",
                new Color(255, 150, 100),
                "Modo MvM - Máquina vs Máquina\n\n" +
                        "Configuras ambos equipos CPU y observas la batalla.\n" +
                        "Para cada CPU configurarás:\n" +
                        "- Estrategia (Defensiva, Ofensiva, etc.)\n" +
                        "- 6 Pokémon y sus movimientos\n" +
                        "- Ítems disponibles");

        pvpButton.addActionListener(e -> controller.showPlayerSetup(1));
        pvmButton.addActionListener(e -> controller.showPlayerSetup(2));
        mvmButton.addActionListener(e -> controller.showPlayerSetup(3));

        buttonPanel.add(pvpButton);
        buttonPanel.add(pvmButton);
        buttonPanel.add(mvmButton);

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(240, 240, 240));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel infoLabel = new JLabel("<html><div style='text-align:center;'>" +
                "<b>Bienvenidos :)</b><br>" +
                "<br>" +
                "</div></html>");
        infoPanel.add(infoLabel);

        menuPanel.add(logoPanel, BorderLayout.NORTH);
        menuPanel.add(buttonPanel, BorderLayout.CENTER);
        menuPanel.add(infoPanel, BorderLayout.SOUTH);
        menuPanel.setBackground(new Color(194, 255, 82));

        add(menuPanel);
    }

    /**
     * Creates a styled button for game mode selection with hover effects and tooltip.
     *
     * @param text        The text to display on the button
     * @param bgColor     The background color of the button
     * @param description The description to show in the tooltip
     * @return            A configured JButton instance
     */
    private JButton createModeButton(String text, Color bgColor, String description) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setToolTipText(description);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Sets up the battle window interface, replacing the main menu.
     * Initializes all necessary UI components for the battle screen.
     */
    public void setupBattleWindow() {
        getContentPane().removeAll();
        setTitle("POOBkemon Battle");
        setSize(700, 600);
        prepareElements();
        prepareListeners();
        revalidate();
        repaint();
    }

    /**
     * Updates the battle interface with current game state information.
     * Updates health bars, Pokémon sprites, turn indicators, and enables/disables buttons.
     *
     * @param state The current state of the battle containing all relevant information
     */
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

    /**
     * Updates the display information for a specific Pokémon.
     * Handles HP bar color changes based on remaining health and visual indicators for fainted Pokémon.
     *
     * @param pokemon   The Pokémon whose information should be updated
     * @param infoLabel The label for displaying Pokémon name and level
     * @param hpBar     The progress bar representing Pokémon's HP
     * @param panel     The panel containing the Pokémon's display elements
     */
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

    /**
     * Loads and displays a Pokémon sprite image.
     * Attempts to find the sprite file in several common formats.
     * Falls back to text display if image cannot be loaded.
     *
     * @param label       The JLabel where the sprite will be displayed
     * @param pokemonName The name of the Pokémon (used for filename)
     */
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

    /**
     * Prepares and initializes all UI components for the battle window.
     * Creates the layout structure and adds all necessary elements.
     */
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
        turnTimerLabel = new JLabel("Tiempo restante: 20s");
        turnTimerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panelInfo.add(turnTimerLabel);

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

    /**
     * Updates the turn timer display with the remaining time.
     *
     * @param secondsLeft The number of seconds remaining in the current turn
     */
    public void updateTurnTimer(int secondsLeft) {
        turnTimerLabel.setText("Tiempo restante: " + secondsLeft + "s");
    }

    /**
     * Sets up action listeners for all interactive buttons in the battle UI.
     * Connects button clicks to controller methods.
     */
    private void prepareListeners() {
        btnAtacar.addActionListener(e -> controller.showAttackOptions());

        btnCambiar.addActionListener(e -> controller.showSwitchPokemonDialog());

        btnItem.addActionListener(e -> controller.showItemSelectionDialog());

        btnExtra.addActionListener(e -> controller.handleSurrender());
    }

    /**
     * Displays attack options for the current Pokémon.
     * Creates buttons for each available move and handles their selection.
     *
     * @param moves A list of moves available to the current Pokémon
     */
    public void showAttackOptions(List<Move> moves) {
        JPanel attackPanel = (JPanel) panelOpciones.getComponent(1);
        attackPanel.removeAll();
        attackPanel.setLayout(new GridLayout(0, 1, 5, 5));

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

    /**
     * Sets the game controller for this GUI.
     * Establishes the connection between view and controller.
     *
     * @param controller The GameController instance to use
     */
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Displays a description of the selected game mode in a dialog window.
     *
     * @param message The description text to display
     */
    private void showModeDescription(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Descripción del Modo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows the battle end message and exits the application.
     *
     * @param message The result message to display
     */
    public void showBattleEnd(String message) {
        JOptionPane.showMessageDialog(this, message);
        System.exit(0);
    }

    /**
     * Checks if the attack panel is currently visible.
     *
     * @return true if the attack panel is visible, false otherwise
     */
    public boolean isAttackPanelVisible() {
        return ((JPanel)panelOpciones.getComponent(1)).getComponentCount() > 0;
    }

    /**
     * Shows the main options panel by switching the card layout.
     */
    public void showMainOptions() {
        cardLayout.show(panelOpciones, "main");
    }

    public void setGameMode(int mode) {
        this.gameMode = mode;
    }

    /**
     * Muestra la pantalla inicial de selección de modo.
     * Este método reemplaza la llamada directa al menú principal.
     */
    public void showInitialScreen() {
        setVisible(false);
        ModeSelectionGUI selector = new ModeSelectionGUI(this);
        selector.setVisible(true);
    }

    /**
     * Método llamado desde ModeSelectionGUI cuando se selecciona el modo normal.
     * Muestra la pantalla de selección de modo de juego (PvP, PvM, MvM).
     */
    public void showGameModeSelection() {
        setVisible(true);
    }

    /**
     * Método llamado desde ModeSelectionGUI cuando se selecciona el modo supervivencia.
     * Inicia directamente una partida en modo supervivencia.
     */
    public void startSurvivalGame() {
        setVisible(false);

        String player1 = JOptionPane.showInputDialog(this, "Nombre del Jugador 1:");
        if (player1 == null || player1.trim().isEmpty()) {
            player1 = "Jugador 1";
        }

        String player2 = JOptionPane.showInputDialog(this, "Nombre del Jugador 2:");
        if (player2 == null || player2.trim().isEmpty()) {
            player2 = "Jugador 2";
        }

        setupBattleWindow();
        setVisible(true);
        controller.startSurvivalMode(player1, player2);
    }

    /**
     * The main method to launch the application.
     * Creates a GUI instance and muestra primero la selección de modo.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BattleGUI gui = new BattleGUI();
            gui.showInitialScreen();
        });
    }
}