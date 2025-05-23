package presentation;

import domain.*;
import presentation.components.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

/**
 * Clase principal de la interfaz de batalla, refactorizada con responsabilidades separadas.
 * Se encarga de coordinar los diferentes componentes de la UI especializados.
 *
 * Autores: Diego Chavarro, Diego Rodríguez
 */
public class BattleGUI extends JFrame implements BattleEventListener {
    // Componentes especializados
    private MainMenuPanel mainMenuPanel;
    private SpriteManager spriteManager;
    private GamePersistenceManager persistenceManager;
    private PauseManager pauseManager;

    // Componentes de batalla
    private PokemonInfoPanel pokemonInfo1, pokemonInfo2;
    private JLabel pok1Label, pok2Label;
    private BattleLogPanel logPanel;
    private JPanel panelOpciones;
    private CardLayout cardLayout;
    private JLabel infoLabel, turnTimerLabel;
    private JButton btnAtacar, btnCambiar, btnItem, btnHuir;

    // Estado
    private GameController controller;
    private Font pokemonFont;
    private int gameMode;
    private BackgroundPanel battlePanel;
    private static final int ORIGINAL_WIDTH = 800;
    private static final int ORIGINAL_HEIGHT = 400;

    /**
     * Construye una nueva instancia de BattleGUI que inicializa la ventana del menú principal.
     */
    public BattleGUI() {
        initializeFrame();
        initializeComponents();
        setupMainMenu();
    }

    private void initializeFrame() {
        setTitle("POOBkemon Battle - Menú Principal");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        loadPokemonFont();
        controller = new GameController(this);
    }

    private void loadPokemonFont() {
        try {
            pokemonFont = Font.createFont(Font.TRUETYPE_FONT,
                    new java.io.File("src/sprites/pokemon_font.ttf")).deriveFont(12f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pokemonFont);
        } catch (Exception e) {
            System.err.println("No se pudo cargar la fuente Pokémon, usando fuente por defecto");
            pokemonFont = new Font("Arial", Font.PLAIN, 12);
        }
    }

    private void initializeComponents() {
        spriteManager = new SpriteManager();
        persistenceManager = new GamePersistenceManager(this);
        pauseManager = new PauseManager(this, controller, pokemonFont);
    }

    private void setupMainMenu() {
        mainMenuPanel = new MainMenuPanel(controller, pokemonFont);
        add(mainMenuPanel);
        prepareMenuBar();
        setVisible(true);
    }

    private void prepareMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(80, 160, 200));
        menuBar.setBorder(BorderFactory.createLineBorder(new Color(40, 80, 100), 1));

        // Menú Archivo
        JMenu fileMenu = new JMenu("Archivo");
        fileMenu.setForeground(Color.WHITE);
        fileMenu.setFont(pokemonFont.deriveFont(Font.BOLD, 14));

        JMenuItem saveItem = new JMenuItem("Guardar partida");
        saveItem.setFont(pokemonFont);
        saveItem.addActionListener(e -> saveGame());

        JMenuItem loadItem = new JMenuItem("Cargar partida");
        loadItem.setFont(pokemonFont);
        loadItem.addActionListener(e -> loadGame());

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);

        // Menú Pausa
        JMenu pauseMenu = new JMenu("Pausa");
        pauseMenu.setForeground(Color.WHITE);
        pauseMenu.setFont(pokemonFont.deriveFont(Font.BOLD, 14));

        JMenuItem pauseItem = new JMenuItem("Pausar/Reanudar");
        pauseItem.setFont(pokemonFont);
        pauseItem.addActionListener(e -> togglePause());

        pauseMenu.add(pauseItem);
        menuBar.add(pauseMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Configura la ventana de batalla
     */
    public void setupBattleWindow() {
        getContentPane().removeAll();
        setTitle("POOBkemon Battle");
        setSize(800, 600);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        createBattleComponents();
        layoutBattleComponents();
        setupBattleListeners();

        // Listener para redimensionamiento
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (battlePanel != null) {
                    repositionBattleElements();
                }
            }
        });

        revalidate();
        repaint();
    }

    private void createBattleComponents() {
        // Panel principal de batalla
        battlePanel = new BackgroundPanel();
        battlePanel.setPreferredSize(new Dimension(800, 400));

        // Componentes de información de Pokémon usando los nuevos componentes especializados
        pokemonInfo1 = new PokemonInfoPanel(pokemonFont);
        pokemonInfo2 = new PokemonInfoPanel(pokemonFont);

        // Labels para sprites
        pok1Label = new JLabel("", JLabel.CENTER);
        pok2Label = new JLabel("", JLabel.CENTER);

        // Panel de log
        logPanel = new BattleLogPanel();
        logPanel.setPreferredSize(new Dimension(getWidth(), 50));
        logPanel.setBackground(new Color(64, 120, 192));
        logPanel.setForeground(Color.WHITE);
        logPanel.setFont(pokemonFont.deriveFont(14f));

        // Crear componentes de control
        createControlComponents();
    }

    private void createControlComponents() {
        // Panel de información y timer
        JPanel panelInfo = new JPanel(new GridBagLayout());
        panelInfo.setPreferredSize(new Dimension(300, 100));
        panelInfo.setBackground(new Color(255, 255, 200));
        panelInfo.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        infoLabel = new JLabel("", JLabel.CENTER);
        infoLabel.setFont(pokemonFont);
        panelInfo.add(infoLabel, gbc);

        gbc.gridy = 1;
        turnTimerLabel = new JLabel("Tiempo restante: 20s", JLabel.CENTER);
        turnTimerLabel.setFont(pokemonFont.deriveFont(Font.BOLD, 16));
        panelInfo.add(turnTimerLabel, gbc);

        // Panel de opciones con CardLayout
        cardLayout = new CardLayout();
        panelOpciones = new JPanel(cardLayout);
        panelOpciones.setPreferredSize(new Dimension(450, 110));

        // Crear botones de batalla
        createBattleButtons();
    }

    private void createBattleButtons() {
        // Panel principal de opciones
        JPanel mainOptionsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        mainOptionsPanel.setBackground(new Color(200, 224, 248));

        btnAtacar = createBattleButton("ATACAR", new Color(200, 60, 60));
        btnCambiar = createBattleButton("CAMBIAR", new Color(60, 140, 200));
        btnItem = createBattleButton("USAR ÍTEM", new Color(60, 200, 60));
        btnHuir = createBattleButton("HUIR", new Color(200, 160, 60));

        mainOptionsPanel.add(btnAtacar);
        mainOptionsPanel.add(btnCambiar);
        mainOptionsPanel.add(btnItem);
        mainOptionsPanel.add(btnHuir);

        // Panel vacío para opciones de ataque
        JPanel attackOptionsPanel = new JPanel();
        attackOptionsPanel.setBackground(new Color(200, 224, 248));

        panelOpciones.add(mainOptionsPanel, "main");
        panelOpciones.add(attackOptionsPanel, "attacks");
    }

    private JButton createBattleButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(pokemonFont.deriveFont(Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

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

    private void layoutBattleComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(120, 184, 232));

        // Agregar componentes al panel de batalla
        battlePanel.add(pokemonInfo1);
        battlePanel.add(pokemonInfo2);
        battlePanel.add(pok1Label);
        battlePanel.add(pok2Label);

        // Posicionar elementos inicialmente
        pokemonInfo1.setBounds(20, 20, 250, 80);
        pokemonInfo2.setBounds(530, 20, 250, 80);
        pok1Label.setBounds(80, 220, 200, 200);
        pok2Label.setBounds(500, 90, 200, 200);

        // Panel inferior
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(200, 224, 248));
        panelInferior.setBorder(BorderFactory.createLineBorder(new Color(64, 120, 192), 3));

        // Crear panel de información
        JPanel panelInfo = createInfoPanel();
        panelInferior.add(panelInfo, BorderLayout.WEST);
        panelInferior.add(panelOpciones, BorderLayout.CENTER);

        // Panel inferior completo
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(logPanel, BorderLayout.NORTH);
        bottomPanel.add(panelInferior, BorderLayout.SOUTH);

        // Ensamblar panel principal
        mainPanel.add(battlePanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createInfoPanel() {
        JPanel panelInfo = new JPanel(new GridBagLayout());
        panelInfo.setPreferredSize(new Dimension(300, 100));
        panelInfo.setBackground(new Color(255, 255, 200));
        panelInfo.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        panelInfo.add(infoLabel, gbc);

        gbc.gridy = 1;
        panelInfo.add(turnTimerLabel, gbc);

        return panelInfo;
    }

    private void setupBattleListeners() {
        btnAtacar.addActionListener(e -> controller.showAttackOptions());
        btnCambiar.addActionListener(e -> controller.showSwitchPokemonDialog());
        btnItem.addActionListener(e -> controller.showItemSelectionDialog());
        btnHuir.addActionListener(e -> controller.handleSurrender());
    }

    /**
     * Actualiza la información de batalla usando los componentes especializados
     */
    public void updateBattleInfo(BattleState state) {
        // Usar los componentes especializados para actualizar información de Pokémon
        pokemonInfo1.updatePokemonInfo(state.getPlayer1Pokemon());
        pokemonInfo2.updatePokemonInfo(state.getPlayer2Pokemon());

        // Indicar cuál es el turno activo
        pokemonInfo1.setTurnActive(state.isPlayer1Turn());
        pokemonInfo2.setTurnActive(!state.isPlayer1Turn());

        // Cargar sprites usando SpriteManager
        spriteManager.loadPokemonSprite(pok1Label,
                state.getPlayer1Pokemon().getName().toLowerCase(), true);
        spriteManager.loadPokemonSprite(pok2Label,
                state.getPlayer2Pokemon().getName().toLowerCase(), false);

        updateTurnInfo(state);
        updateButtonStates(state);
    }

    private void updateTurnInfo(BattleState state) {
        String turnInfo = "Turno de " + state.getCurrentPlayerName();
        Color turnColor = state.isPlayer1Turn() ?
                new Color(50, 150, 250) : new Color(250, 50, 50);

        String statusText = "<html><div style='text-align:center;color:black;'>" +
                "<b><font color='" + String.format("#%02x%02x%02x",
                turnColor.getRed(), turnColor.getGreen(), turnColor.getBlue()) + "'>" +
                turnInfo + "</font></b><br>" +
                state.getPlayer1Name() + ": " +
                "<b>" + state.getPlayer1Pokemon().getName() + "</b> (" +
                state.getPlayer1Pokemon().getHp() + "/" +
                state.getPlayer1Pokemon().getMaxHp() + " HP)<br>" +
                state.getPlayer2Name() + ": " +
                "<b>" + state.getPlayer2Pokemon().getName() + "</b> (" +
                state.getPlayer2Pokemon().getHp() + "/" +
                state.getPlayer2Pokemon().getMaxHp() + " HP)";

        if (state.getClimate() != null) {
            statusText += "<br>Clima: <i>" + state.getClimate() + "</i>";
        }

        statusText += "</div></html>";
        infoLabel.setText(statusText);
        infoLabel.setFont(pokemonFont);
    }

    private void updateButtonStates(BattleState state) {
        boolean isHumanTurn = state.isHumanTurn();
        btnAtacar.setEnabled(isHumanTurn && !isPaused());
        btnCambiar.setEnabled(isHumanTurn && !isPaused());
        btnItem.setEnabled(isHumanTurn && !isPaused());
        btnHuir.setEnabled(isHumanTurn && !isPaused());
    }

    public void showAttackOptions(List<Move> moves) {
        JPanel attackPanel = (JPanel) panelOpciones.getComponent(1);
        attackPanel.removeAll();

        int rows = Math.min(5, moves.size() + 1);
        attackPanel.setLayout(new GridLayout(rows, 1, 5, 5));

        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            JButton moveButton = createBattleButton(
                    move.name() + " (PP: " + move.pp() + "/" + move.maxPP() + ")",
                    new Color(200, 120, 200));

            final int moveIndex = i;
            moveButton.addActionListener(e -> controller.executeAttack(moveIndex));

            if (move.pp() <= 0) {
                moveButton.setEnabled(false);
                moveButton.setBackground(Color.GRAY);
            }

            attackPanel.add(moveButton);
        }

        JButton cancelButton = createBattleButton("CANCELAR", new Color(160, 160, 160));
        cancelButton.addActionListener(e -> cardLayout.show(panelOpciones, "main"));
        attackPanel.add(cancelButton);

        cardLayout.show(panelOpciones, "attacks");
        attackPanel.revalidate();
        attackPanel.repaint();
    }

    public void updateTurnTimer(int secondsLeft) {
        turnTimerLabel.setText("Tiempo restante: " + secondsLeft + "s");
    }

    // Métodos delegados a componentes especializados
    private void saveGame() {
        persistenceManager.saveGame(controller, gameMode);
    }

    private void loadGame() {
        GameState gameState = persistenceManager.loadGame();
        if (gameState != null) {
            controller.loadGameState(gameState);
            this.gameMode = gameState.getGameMode();
            setupBattleWindow();
            updateBattleInfo(gameState.getBattle().getBattleState());
        }
    }

    private void togglePause() {
        pauseManager.togglePause();
    }

    public boolean isPaused() {
        return pauseManager.isPaused();
    }

    // Métodos de utilidad y configuración
    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void setGameMode(int mode) {
        this.gameMode = mode;
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

    public BattleLogPanel getBattleLogPanel() {
        return logPanel;
    }

    // Implementación de BattleEventListener
    @Override
    public void onAttackPerformed(String attackerName, String targetName, String moveName) {
        logPanel.addMessage(attackerName + " atacó a " + targetName + " con " + moveName + "!");
    }

    @Override
    public void onItemUsed(String playerName, String itemName, String targetName) {
        logPanel.addMessage(playerName + " usó " + itemName + " en " + targetName + "!");
    }

    @Override
    public void onPokemonSwitched(String playerName, String pokemonName) {
        logPanel.addMessage(playerName + " envió a " + pokemonName + "!");
    }

    @Override
    public void onDamageReceived(String pokemonName, int damage) {
        logPanel.addMessage(pokemonName + " perdió " + damage + " PS!");
    }

    @Override
    public void onPokemonFainted(String pokemonName) {
        logPanel.addMessage(pokemonName + " se debilitó!");
    }

    // Clase interna para el panel de fondo con imagen
    private class BackgroundPanel extends JPanel {
        private java.awt.image.BufferedImage backgroundImage;

        public BackgroundPanel() {
            setLayout(null); // Layout absoluto para posicionamiento manual
            loadBackgroundImage();
        }

        private void loadBackgroundImage() {
            backgroundImage = spriteManager.loadBackgroundImage("battleBackground.png");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            } else {
                g.setColor(new Color(120, 184, 232));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    private void repositionBattleElements() {
        if (battlePanel == null) return;

        int battleWidth = battlePanel.getWidth();
        int battleHeight = battlePanel.getHeight();

        if (battleWidth <= 0 || battleHeight <= 0) return;

        // Calcular factores de escala
        double scaleX = (double) battleWidth / ORIGINAL_WIDTH;
        double scaleY = (double) battleHeight / ORIGINAL_HEIGHT;

        // Reposicionar componentes de información de Pokémon
        if (pokemonInfo1 != null) {
            int x1 = (int) (20 * scaleX);
            int y1 = (int) (20 * scaleY);
            int w1 = (int) (250 * scaleX);
            int h1 = (int) (80 * scaleY);
            pokemonInfo1.setBounds(x1, y1, w1, h1);
        }

        if (pokemonInfo2 != null) {
            int x2 = (int) ((ORIGINAL_WIDTH - 270) * scaleX);
            int y2 = (int) (20 * scaleY);
            int w2 = (int) (250 * scaleX);
            int h2 = (int) (80 * scaleY);
            pokemonInfo2.setBounds(x2, y2, w2, h2);
        }

        // Reposicionar sprites de Pokémon
        if (pok1Label != null) {
            int x1 = (int) (80 * scaleX);
            int y1 = (int) (220 * scaleY);
            int w1 = (int) (200 * scaleX);
            int h1 = (int) (200 * scaleY);
            pok1Label.setBounds(x1, y1, w1, h1);
        }

        if (pok2Label != null) {
            int x2 = (int) ((ORIGINAL_WIDTH - 300) * scaleX);
            int y2 = (int) (90 * scaleY);
            int w2 = (int) (200 * scaleX);
            int h2 = (int) (200 * scaleY);
            pok2Label.setBounds(x2, y2, w2, h2);
        }

        battlePanel.revalidate();
        battlePanel.repaint();
    }

    // Métodos para compatibilidad con otras partes del sistema
    public void showInitialScreen() {
        setVisible(false);
        ModeSelectionGUI selector = new ModeSelectionGUI(this);
        selector.setVisible(true);
    }

    public void showGameModeSelection() {
        setVisible(true);
    }

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BattleGUI gui = new BattleGUI();
            gui.showInitialScreen();
        });
    }
}