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
 * La clase BattleGUI representa la interfaz gráfica para el juego de batalla de Pokémon.
 * Se encarga exclusivamente de mostrar la escena de combate y gestionar la interacción con el usuario.
 * Controla el flujo visual de la partida, la carga de sprites, las barras de vida, y los botones de acción.
 *
 * Autores: Diego Chavarro, Diego Rodríguez
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
     * Construye una nueva instancia de BattleGUI que inicializa la ventana del menú principal.
     * Configura las propiedades predeterminadas de la ventana y prepara la interfaz del menú.
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
     * Crea un botón para la selección del modo de juego, con efectos al pasar el cursor y descripción emergente.
     *
     * @param text        El texto que se mostrará en el botón
     * @param bgColor     El color de fondo del botón
     * @param description La descripción que se mostrará como ayuda emergente (tooltip)
     * @return            Una instancia de JButton configurada
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
     * Configura la interfaz de la ventana de batalla, reemplazando el menú principal.
     * Inicializa todos los componentes gráficos necesarios para la pantalla de combate.
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
     * Actualiza la interfaz de batalla con la información actual del estado del juego.
     * Refresca las barras de vida, los sprites de los Pokémon, el indicador de turno y habilita o deshabilita los botones.
     *
     * @param state El estado actual de la batalla que contiene toda la información relevante
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
     * Actualiza la información visual de un Pokémon específico.
     * Gestiona el color de la barra de vida según la salud restante y los indicadores visuales si el Pokémon está debilitado.
     *
     * @param pokemon   El Pokémon cuya información debe actualizarse
     * @param infoLabel La etiqueta que muestra el nombre y nivel del Pokémon
     * @param hpBar     La barra de progreso que representa los puntos de vida del Pokémon
     * @param panel     El panel que contiene los elementos visuales del Pokémon
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
     * Carga y muestra la imagen del sprite de un Pokémon.
     * Intenta encontrar el archivo del sprite en varios formatos comunes.
     * Si no se puede cargar la imagen, muestra el nombre del Pokémon como texto.
     *
     * @param label       El JLabel donde se mostrará el sprite
     * @param pokemonName El nombre del Pokémon (usado para buscar el archivo)
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
     * Prepara e inicializa todos los componentes de la interfaz para la ventana de batalla.
     * Crea la estructura del diseño y añade todos los elementos necesarios.
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
     * Actualiza la visualización del temporizador de turno con el tiempo restante.
     *
     * @param secondsLeft La cantidad de segundos restantes en el turno actual
     */

    public void updateTurnTimer(int secondsLeft) {
        turnTimerLabel.setText("Tiempo restante: " + secondsLeft + "s");
    }

    /**
     * Configura los escuchadores de eventos para todos los botones interactivos en la interfaz de batalla.
     * Conecta los clics de los botones con los métodos del controlador.
     */

    private void prepareListeners() {
        btnAtacar.addActionListener(e -> controller.showAttackOptions());

        btnCambiar.addActionListener(e -> controller.showSwitchPokemonDialog());

        btnItem.addActionListener(e -> controller.showItemSelectionDialog());

        btnExtra.addActionListener(e -> controller.handleSurrender());
    }

    /**
     * Muestra las opciones de ataque disponibles para el Pokémon actual.
     * Crea botones para cada movimiento disponible y gestiona su selección.
     *
     * @param moves Una lista de movimientos disponibles para el Pokémon actual
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
     * Establece el controlador del juego para esta interfaz gráfica.
     * Establece la conexión entre la vista y el controlador.
     *
     * @param controller La instancia de GameController que se va a utilizar
     */

    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Muestra una descripción del modo de juego seleccionado en una ventana de diálogo.
     *
     * @param message El texto descriptivo que se va a mostrar
     */
    private void showModeDescription(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Descripción del Modo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra el mensaje de finalización de la batalla y cierra la aplicación.
     *
     * @param message El mensaje con el resultado que se va a mostrar
     */
    public void showBattleEnd(String message) {
        JOptionPane.showMessageDialog(this, message);
        System.exit(0);
    }

    /**
     * Verifica si el panel de ataque está actualmente visible.
     *
     * @return true si el panel de ataque está visible, false en caso contrario
     */
    public boolean isAttackPanelVisible() {
        return ((JPanel)panelOpciones.getComponent(1)).getComponentCount() > 0;
    }

    /**
     * Muestra el panel de opciones principales cambiando el diseño con el CardLayout.
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
     * Método principal para iniciar la aplicación.
     * Crea una instancia de la interfaz gráfica y muestra primero la pantalla de selección de modo.
     *
     * @param args Argumentos de línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BattleGUI gui = new BattleGUI();
            gui.showInitialScreen();
        });
    }
}