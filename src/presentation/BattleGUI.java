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

    /**
     * Inicializa la ventana principal con configuración básica:
     * - Establece el título de la aplicación
     * - Define el tamaño inicial (500x400)
     * - Centra la ventana en la pantalla
     * - Configura el comportamiento al cerrar (terminar aplicación)
     * - Carga la fuente personalizada del juego
     * - Inicializa el controlador principal del juego
     */
    private void initializeFrame() {
        setTitle("POOBkemon Battle - Menú Principal");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        loadPokemonFont();
        controller = new GameController(this);
    }

    /**
     * Carga la fuente personalizada del juego desde un archivo TTF.
     * Si no puede cargar la fuente, usa Arial como fallback.
     * Registra la fuente en el entorno gráfico para su uso global.
     */
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

    /**
     * Inicializa los componentes principales del juego:
     * - Gestor de sprites para imágenes del juego
     * - Gestor de persistencia para guardar/cargar partidas
     * - Gestor de pausa para controlar el estado del juego
     */
    private void initializeComponents() {
        spriteManager = new SpriteManager();
        persistenceManager = new GamePersistenceManager(this);
        pauseManager = new PauseManager(this, controller, pokemonFont);
    }

    /**
     * Configura y muestra el menú principal del juego:
     * - Crea el panel del menú principal con el controlador y fuente
     * - Añade el panel a la ventana principal
     * - Prepara la barra de menú superior
     * - Hace visible la ventana
     */
    private void setupMainMenu() {
        mainMenuPanel = new MainMenuPanel(controller, pokemonFont);
        add(mainMenuPanel);
        prepareMenuBar();
        setVisible(true);
    }

    /**
     * Prepara la barra de menú superior con:
     * - Menú Archivo (opciones Guardar/Cargar partida)
     * - Menú Pausa (opción Pausar/Reanudar)
     * Configura los estilos visuales y los listeners de acción
     */
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
     * Configura la ventana para el modo batalla:
     * - Limpia el contenido actual
     * - Establece título y tamaño (800x600)
     * - Crea y organiza los componentes de batalla
     * - Configura listeners para eventos
     * - Añade listener para redimensionamiento
     * - Actualiza la interfaz gráfica
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

    /**
     * Crea los componentes principales de la interfaz de batalla:
     * - Panel de fondo con imagen
     * - Paneles de información de Pokémon (jugador y oponente)
     * - Labels para mostrar los sprites de los Pokémon
     * - Panel de registro de eventos (log)
     * - Componentes de control (botones, etc.)
     */
    private void createBattleComponents() {
        battlePanel = new BackgroundPanel();
        battlePanel.setPreferredSize(new Dimension(800, 400));

        pokemonInfo1 = new PokemonInfoPanel(pokemonFont);
        pokemonInfo2 = new PokemonInfoPanel(pokemonFont);

        pok1Label = new JLabel("", JLabel.CENTER);
        pok2Label = new JLabel("", JLabel.CENTER);

        logPanel = new BattleLogPanel();
        logPanel.setPreferredSize(new Dimension(getWidth(), 50));
        logPanel.setBackground(new Color(64, 120, 192));
        logPanel.setForeground(Color.WHITE);
        logPanel.setFont(pokemonFont.deriveFont(14f));

        createControlComponents();
    }

    /**
     * Crea los componentes de control de la batalla:
     * - Panel de información con temporizador
     * - Panel de opciones con CardLayout para alternar vistas
     * - Botones de acciones principales (Atacar, Cambiar, etc.)
     */
    private void createControlComponents() {
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

        cardLayout = new CardLayout();
        panelOpciones = new JPanel(cardLayout);
        panelOpciones.setPreferredSize(new Dimension(450, 110));

        createBattleButtons();
    }

    /**
     * Crea los botones principales de acciones de batalla:
     * - Atacar (rojo)
     * - Cambiar Pokémon (azul)
     * - Usar Ítem (verde)
     * - Huir (amarillo)
     * Configura sus propiedades visuales y los añade al panel
     */
    private void createBattleButtons() {
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

        JPanel attackOptionsPanel = new JPanel();
        attackOptionsPanel.setBackground(new Color(200, 224, 248));

        panelOpciones.add(mainOptionsPanel, "main");
        panelOpciones.add(attackOptionsPanel, "attacks");
    }

    /**
     * Crea un botón de batalla con estilo personalizado:
     * - Fuente específica del juego
     * - Color de fondo personalizado
     * - Efecto hover (brillo al pasar el ratón)
     * - Borde y padding adecuados
     *
     * @param text Texto a mostrar en el botón
     * @param bgColor Color de fondo del botón
     * @return JButton configurado con el estilo de batalla
     */
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

    /**
     * Organiza los componentes de la interfaz de batalla:
     * - Distribuye los paneles de información de Pokémon
     * - Posiciona los sprites de los Pokémon
     * - Configura el panel inferior con log y opciones
     * - Ensambla todos los componentes en el layout principal
     */
    private void layoutBattleComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(120, 184, 232));

        battlePanel.add(pokemonInfo1);
        battlePanel.add(pokemonInfo2);
        battlePanel.add(pok1Label);
        battlePanel.add(pok2Label);

        pokemonInfo1.setBounds(20, 20, 250, 80);
        pokemonInfo2.setBounds(530, 20, 250, 80);
        pok1Label.setBounds(80, 220, 200, 200);
        pok2Label.setBounds(500, 90, 200, 200);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(200, 224, 248));
        panelInferior.setBorder(BorderFactory.createLineBorder(new Color(64, 120, 192), 3));

        JPanel panelInfo = createInfoPanel();
        panelInferior.add(panelInfo, BorderLayout.WEST);
        panelInferior.add(panelOpciones, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(logPanel, BorderLayout.NORTH);
        bottomPanel.add(panelInferior, BorderLayout.SOUTH);

        mainPanel.add(battlePanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Crea y configura el panel de información de batalla.
     * @return JPanel configurado con:
     *         - Layout GridBag para organización flexible
     *         - Fondo amarillo claro
     *         - Borde gris
     *         - Contiene las etiquetas de información y temporizador
     */
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

    /**
     * Configura los listeners para los botones de batalla:
     * - Atacar: Muestra opciones de ataque
     * - Cambiar: Abre diálogo para cambiar Pokémon
     * - Ítem: Abre diálogo para seleccionar ítem
     * - Huir: Maneja la rendición del jugador
     */
    private void setupBattleListeners() {
        btnAtacar.addActionListener(e -> controller.showAttackOptions());
        btnCambiar.addActionListener(e -> controller.showSwitchPokemonDialog());
        btnItem.addActionListener(e -> controller.showItemSelectionDialog());
        btnHuir.addActionListener(e -> controller.handleSurrender());
    }

    /**
     * Actualiza toda la información visual de la batalla.
     * @param state Estado actual de la batalla que contiene:
     *              - Información de los Pokémon
     *              - Turno actual
     *              - Nombres de jugadores
     */
    public void updateBattleInfo(BattleState state) {
        pokemonInfo1.updatePokemonInfo(state.getPlayer1Pokemon());
        pokemonInfo2.updatePokemonInfo(state.getPlayer2Pokemon());

        pokemonInfo1.setTurnActive(state.isPlayer1Turn());
        pokemonInfo2.setTurnActive(!state.isPlayer1Turn());

        spriteManager.loadPokemonSprite(pok1Label,
                state.getPlayer1Pokemon().getName().toLowerCase(), true);
        spriteManager.loadPokemonSprite(pok2Label,
                state.getPlayer2Pokemon().getName().toLowerCase(), false);

        updateTurnInfo(state);
        updateButtonStates(state);
    }

    /**
     * Actualiza la información del turno actual con:
     * - Nombre del jugador activo (color diferenciado)
     * - HP actual/máximo de ambos Pokémon
     * - Clima actual si existe
     * @param state Estado de la batalla con la información a mostrar
     */
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

    /**
     * Actualiza el estado de los botones según:
     * - Si es turno del jugador humano
     * - Si el juego está pausado
     * @param state Estado actual de la batalla
     */
    private void updateButtonStates(BattleState state) {
        boolean isHumanTurn = state.isHumanTurn();
        btnAtacar.setEnabled(isHumanTurn && !isPaused());
        btnCambiar.setEnabled(isHumanTurn && !isPaused());
        btnItem.setEnabled(isHumanTurn && !isPaused());
        btnHuir.setEnabled(isHumanTurn && !isPaused());
    }

    /**
     * Muestra las opciones de ataque disponibles:
     * - Crea botones para cada movimiento
     * - Deshabilita movimientos sin PP
     * - Añade botón de cancelar
     * @param moves Lista de movimientos disponibles
     */
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

    /**
     * Actualiza el temporizador de turno.
     * @param secondsLeft Segundos restantes para el turno actual
     */
    public void updateTurnTimer(int secondsLeft) {
        turnTimerLabel.setText("Tiempo restante: " + secondsLeft + "s");
    }

    /**
     * Delega el guardado de partida al PersistenceManager.
     * @see GamePersistenceManager#saveGame(GameController, int)
     */
    private void saveGame() {
        persistenceManager.saveGame(controller, gameMode);
    }

    /**
     * Delega la carga de partida al PersistenceManager.
     * @see GamePersistenceManager#loadGame()
     */
    private void loadGame() {
        GameState gameState = persistenceManager.loadGame();
        if (gameState != null) {
            controller.loadGameState(gameState);
            this.gameMode = gameState.getGameMode();
            setupBattleWindow();
            updateBattleInfo(gameState.getBattle().getBattleState());
        }
    }

    /**
     * Alterna el estado de pausa del juego.
     * @see PauseManager#togglePause()
     */
    private void togglePause() {
        pauseManager.togglePause();
    }

    /**
     * Verifica si el juego está pausado.
     * @return true si el juego está pausado, false en caso contrario
     * @see PauseManager#isPaused()
     */
    public boolean isPaused() {
        return pauseManager.isPaused();
    }

    /**
     * Establece el controlador del juego.
     * @param controller Controlador principal del juego
     */
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Establece el modo de juego actual.
     * @param mode Modo de juego (1=PvP, 2=PvM, 3=MvM)
     */
    public void setGameMode(int mode) {
        this.gameMode = mode;
    }

    /**
     * Muestra el mensaje de fin de batalla y termina la aplicación.
     * @param message Mensaje con el resultado de la batalla
     */
    public void showBattleEnd(String message) {
        JOptionPane.showMessageDialog(this, message);
        System.exit(0);
    }

    /**
     * Verifica si el panel de ataques está visible.
     * @return true si hay ataques mostrados, false en caso contrario
     */
    public boolean isAttackPanelVisible() {
        return ((JPanel)panelOpciones.getComponent(1)).getComponentCount() > 0;
    }

    /**
     * Muestra las opciones principales de batalla.
     */
    public void showMainOptions() {
        cardLayout.show(panelOpciones, "main");
    }

    /**
     * Obtiene el panel de registro de batalla.
     * @return Instancia de BattleLogPanel
     */
    public BattleLogPanel getBattleLogPanel() {
        return logPanel;
    }

    /**
     * Registra un ataque en el log de batalla.
     * @param attackerName Nombre del atacante
     * @param targetName Nombre del objetivo
     * @param moveName Nombre del movimiento usado
     */
    @Override
    public void onAttackPerformed(String attackerName, String targetName, String moveName) {
        logPanel.addMessage(attackerName + " atacó a " + targetName + " con " + moveName + "!");
    }

    /**
     * Registra uso de ítem en el log de batalla.
     * @param playerName Nombre del jugador
     * @param itemName Nombre del ítem usado
     * @param targetName Nombre del objetivo
     */
    @Override
    public void onItemUsed(String playerName, String itemName, String targetName) {
        logPanel.addMessage(playerName + " usó " + itemName + " en " + targetName + "!");
    }

    /**
     * Registra cambio de Pokémon en el log de batalla.
     * @param playerName Nombre del jugador
     * @param pokemonName Nombre del Pokémon enviado
     */
    @Override
    public void onPokemonSwitched(String playerName, String pokemonName) {
        logPanel.addMessage(playerName + " envió a " + pokemonName + "!");
    }

    /**
     * Registra daño recibido en el log de batalla.
     * @param pokemonName Nombre del Pokémon afectado
     * @param damage Cantidad de daño recibido
     */
    @Override
    public void onDamageReceived(String pokemonName, int damage) {
        logPanel.addMessage(pokemonName + " perdió " + damage + " PS!");
    }

    /**
     * Registra Pokémon debilitado en el log de batalla.
     * @param pokemonName Nombre del Pokémon debilitado
     */
    @Override
    public void onPokemonFainted(String pokemonName) {
        logPanel.addMessage(pokemonName + " se debilitó!");
    }

    /**
     * Panel interno para el fondo de batalla con imagen escalable.
     */
    private class BackgroundPanel extends JPanel {
        private java.awt.image.BufferedImage backgroundImage;

        /**
         * Crea el panel de fondo con layout absoluto.
         */
        public BackgroundPanel() {
            setLayout(null);
            loadBackgroundImage();
        }

        /**
         * Carga la imagen de fondo usando SpriteManager.
         */
        private void loadBackgroundImage() {
            backgroundImage = spriteManager.loadBackgroundImage("battleBackground.png");
        }

        /**
         * Dibuja el fondo escalado o color sólido si no hay imagen.
         * @param g Contexto gráfico para dibujar
         */
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

    /**
     * Reposiciona los elementos de batalla al redimensionar la ventana,
     * manteniendo las proporciones originales.
     */
    private void repositionBattleElements() {
        if (battlePanel == null) return;

        int battleWidth = battlePanel.getWidth();
        int battleHeight = battlePanel.getHeight();

        if (battleWidth <= 0 || battleHeight <= 0) return;

        double scaleX = (double) battleWidth / ORIGINAL_WIDTH;
        double scaleY = (double) battleHeight / ORIGINAL_HEIGHT;

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

    /**
     * Muestra la pantalla inicial de selección de modo de juego.
     */
    public void showInitialScreen() {
        setVisible(false);
        ModeSelectionGUI selector = new ModeSelectionGUI(this);
        selector.setVisible(true);
    }

    /**
     * Muestra la selección de modo de juego.
     */
    public void showGameModeSelection() {
        setVisible(true);
    }

    /**
     * Inicia el juego en modo supervivencia:
     * - Solicita nombres de jugadores
     * - Configura la ventana de batalla
     * - Inicia el controlador en modo supervivencia
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
     * Punto de entrada principal de la aplicación.
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BattleGUI gui = new BattleGUI();
            gui.showInitialScreen();
        });
    }
}