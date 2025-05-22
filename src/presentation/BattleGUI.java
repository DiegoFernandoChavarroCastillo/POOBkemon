package presentation;

import domain.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * La clase BattleGUI representa la interfaz gráfica para el juego de batalla de Pokémon.
 * Se encarga exclusivamente de mostrar la escena de combate y gestionar la interacción con el usuario.
 * Controla el flujo visual de la partida, la carga de sprites, las barras de vida, y los botones de acción.
 *
 * Autores: Diego Chavarro, Diego Rodríguez
 */
public class BattleGUI extends JFrame implements BattleEventListener {
    private JPanel panelSuperior, panelInferior, panelPok1, panelPok2, panelImagenes;
    private JLabel labelInfo1, labelInfo2, infoLabel;
    private JProgressBar hpBar1, hpBar2;
    private JButton btnAtacar, btnCambiar, btnItem, btnHuir;
    private JLabel pok1Label, pok2Label;
    private JPanel panelOpciones;
    private CardLayout cardLayout;
    private GameController controller;
    private JLabel turnTimerLabel;
    private int gameMode;
    private BattleLogPanel logPanel;
    private Font pokemonFont;

    /**
     * Construye una nueva instancia de BattleGUI que inicializa la ventana del menú principal.
     * Configura las propiedades predeterminadas de la ventana y prepara la interfaz del menú.
     */
    public BattleGUI() {
        setTitle("POOBkemon Battle - Menú Principal");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Cargar fuente Pokémon
        try {
            pokemonFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/sprites/pokemon_font.ttf")).deriveFont(12f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pokemonFont);
        } catch (Exception e) {
            System.err.println("No se pudo cargar la fuente Pokémon, usando fuente por defecto");
            pokemonFont = new Font("Arial", Font.PLAIN, 12);
        }

        prepareMenuBar();
        prepareMenu();
        setVisible(true);
        controller = new GameController(this);
        setController(controller);
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

        setJMenuBar(menuBar);
    }

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

    private void prepareMenu() {
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        menuPanel.setBackground(new Color(120, 200, 80)); // Verde Pokémon

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        try {
            BufferedImage logoImage = ImageIO.read(new File("src/sprites/Logo.png"));
            Image scaledLogo = logoImage.getScaledInstance(200, 100, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            logoPanel.add(logoLabel);
            logoPanel.setBackground(new Color(120, 200, 80));
        } catch (IOException e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
            JLabel titleLabel = new JLabel("¡Bienvenido a POOBkemon Battle!", JLabel.CENTER);
            titleLabel.setFont(pokemonFont.deriveFont(Font.BOLD, 24));
            titleLabel.setForeground(Color.WHITE);
            logoPanel.add(titleLabel);
        }

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.setBackground(new Color(120, 200, 80));

        JButton pvpButton = createModeButton("Jugador vs Jugador (PvP)",
                new Color(200, 60, 60), // Rojo Pokémon
                "Modo PvP - Jugador vs Jugador\n\n" +
                        "Ambos jugadores controlan sus Pokémon manualmente.\n" +
                        "Cada jugador seleccionará:\n" +
                        "- 6 Pokémon\n" +
                        "- Movimientos para cada Pokémon\n" +
                        "- Ítems para usar en batalla");

        JButton pvmButton = createModeButton("Jugador vs Máquina (PvM)",
                new Color(60, 140, 200), // Azul Pokémon
                "Modo PvM - Jugador vs Máquina\n\n" +
                        "Tú controlas tu equipo y configuras el equipo de la CPU.\n" +
                        "Seleccionarás:\n" +
                        "- Tus 6 Pokémon y sus movimientos\n" +
                        "- Tus ítems\n" +
                        "- Estrategia de la CPU (Defensiva, Ofensiva, etc.)\n" +
                        "- Pokémon, movimientos e ítems de la CPU");

        JButton mvmButton = createModeButton("Máquina vs Máquina (MvM)",
                new Color(200, 160, 60), // Amarillo Pokémon
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
        infoPanel.setBackground(new Color(80, 160, 200)); // Azul oscuro Pokémon
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 80, 100), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel infoLabel = new JLabel("<html><div style='text-align:center;color:white;'>" +
                "<b>Bienvenidos :)</b><br>" +
                "<br>" +
                "</div></html>");
        infoLabel.setFont(pokemonFont);
        infoPanel.add(infoLabel);

        menuPanel.add(logoPanel, BorderLayout.NORTH);
        menuPanel.add(buttonPanel, BorderLayout.CENTER);
        menuPanel.add(infoPanel, BorderLayout.SOUTH);

        add(menuPanel);
    }

    private JButton createModeButton(String text, Color bgColor, String description) {
        JButton button = new JButton(text);
        button.setFont(pokemonFont.deriveFont(Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setToolTipText(description);
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

    public void setupBattleWindow() {
        getContentPane().removeAll();
        setTitle("POOBkemon Battle");
        setSize(800, 600);
        setLocationRelativeTo(null);
        prepareElements();
        prepareListeners();
        revalidate();
        repaint();
    }

    public void updateBattleInfo(BattleState state) {
        // Determinar qué Pokémon está activo (con turno) y cuál es el oponente
        boolean isPlayer1Turn = state.isPlayer1Turn();
        Pokemon player1Pokemon = state.getPlayer1Pokemon();
        Pokemon player2Pokemon = state.getPlayer2Pokemon();
        String player1Name = state.getPlayer1Name();
        String player2Name = state.getPlayer2Name();

        // Actualizar la información de los Pokémon en sus respectivos paneles (siempre mismas posiciones)
        updatePokemonInfo(player1Pokemon, labelInfo1, hpBar1, panelPok1);
        updatePokemonInfo(player2Pokemon, labelInfo2, hpBar2, panelPok2);

        // Cargar los sprites (siempre mismas posiciones)
        loadPokemonSprite(pok1Label, player1Pokemon.getName().toLowerCase(), true);  // Player 1 (back view)
        loadPokemonSprite(pok2Label, player2Pokemon.getName().toLowerCase(), false); // Player 2 (front view)

        // Resaltar el panel del Pokémon activo según el turno
        if (isPlayer1Turn) {
            panelPok1.setBorder(BorderFactory.createLineBorder(new Color(200, 0, 0), 3));
            panelPok2.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        } else {
            panelPok2.setBorder(BorderFactory.createLineBorder(new Color(200, 0, 0), 3));
            panelPok1.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        }

        String turnInfo = "Turno de " + state.getCurrentPlayerName();
        Color turnColor = isPlayer1Turn ? new Color(50, 150, 250) : new Color(250, 50, 50);

        String statusText = "<html><div style='text-align:center;color:black;'>" +
                "<b><font color='" + String.format("#%02x%02x%02x",
                turnColor.getRed(), turnColor.getGreen(), turnColor.getBlue()) + "'>" +
                turnInfo + "</font></b><br>" +
                player1Name + ": " +
                "<b>" + player1Pokemon.getName() + "</b> (" + player1Pokemon.getHp() + "/" +
                player1Pokemon.getMaxHp() + " HP)<br>" +
                player2Name + ": " +
                "<b>" + player2Pokemon.getName() + "</b> (" + player2Pokemon.getHp() + "/" +
                player2Pokemon.getMaxHp() + " HP)";

        if (state.getClimate() != null) {
            statusText += "<br>Clima: <i>" + state.getClimate() + "</i>";
        }

        statusText += "</div></html>";
        infoLabel.setText(statusText);
        infoLabel.setFont(pokemonFont);

        boolean isHumanTurn = state.isHumanTurn();
        btnAtacar.setEnabled(isHumanTurn);
        btnCambiar.setEnabled(isHumanTurn);
        btnItem.setEnabled(isHumanTurn);
        btnHuir.setEnabled(isHumanTurn);
    }


    private void updatePokemonInfo(Pokemon pokemon, JLabel infoLabel, JProgressBar hpBar, JPanel panel) {
        infoLabel.setText(pokemon.getName() + " Lv." + pokemon.getLevel());
        infoLabel.setFont(pokemonFont.deriveFont(Font.BOLD, 14));

        hpBar.setMaximum(pokemon.getMaxHp());
        hpBar.setValue(Math.max(0, pokemon.getHp()));
        hpBar.setString(pokemon.getHp() + "/" + pokemon.getMaxHp());
        hpBar.setStringPainted(true);
        hpBar.setFont(pokemonFont.deriveFont(12f));
        hpBar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        double hpPercentage = (double) pokemon.getHp() / pokemon.getMaxHp();
        if (hpPercentage > 0.5) {
            hpBar.setForeground(new Color(0, 200, 0)); // Verde brillante
        } else if (hpPercentage > 0.2) {
            hpBar.setForeground(new Color(255, 200, 0)); // Amarillo
        } else {
            hpBar.setForeground(new Color(200, 0, 0)); // Rojo
        }

        if (pokemon.getHp() <= 0) {
            panel.setBackground(new Color(200, 200, 200)); // Gris cuando está debilitado
            hpBar.setForeground(Color.GRAY);
            infoLabel.setForeground(Color.GRAY);
        } else {
            panel.setBackground(new Color(255, 255, 200)); // Amarillo claro Pokémon
            infoLabel.setForeground(Color.BLACK);
        }
    }

    private void loadPokemonSprite(JLabel label, String pokemonName, boolean isBackView) {
        String basePath = "src/sprites/";
        int spriteWidth = 150;
        int spriteHeight = 150;

        // Añadir sufijo según perspectiva
        String suffix = isBackView ? "_back" : "_front";

        try {
            // Intentar cargar con sufijo específico
            File file = new File(basePath + pokemonName + suffix + ".png");
            if (file.exists()) {
                BufferedImage originalImage = ImageIO.read(file);
                Image scaledImage = originalImage.getScaledInstance(spriteWidth, spriteHeight, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledImage));
                return;
            }

            // Intentar sin sufijo
            file = new File(basePath + pokemonName + ".png");
            if (file.exists()) {
                BufferedImage originalImage = ImageIO.read(file);
                Image scaledImage = originalImage.getScaledInstance(spriteWidth, spriteHeight, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledImage));
                return;
            }

            // Si no se encuentra, probar otros formatos
            for (String ext : new String[]{".jpg", ".gif"}) {
                file = new File(basePath + pokemonName + ext);
                if (file.exists()) {
                    BufferedImage originalImage = ImageIO.read(file);
                    Image scaledImage = originalImage.getScaledInstance(spriteWidth, spriteHeight, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(scaledImage));
                    return;
                }
            }

            // Si no se encuentra ninguna imagen, mostrar el nombre
            label.setIcon(null);
            label.setText(pokemonName);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
            label.setFont(pokemonFont.deriveFont(Font.BOLD, 16));

        } catch (IOException e) {
            System.err.println("Error al cargar imagen: " + e.getMessage());
            label.setIcon(null);
            label.setText(pokemonName);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
            label.setFont(pokemonFont.deriveFont(Font.BOLD, 16));
        }
    }


    private void prepareElements() {
        // Panel principal con fondo azul como en Pokémon Esmeralda
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(120, 184, 232)); // Azul cielo Pokémon

        // Panel de batalla que contendrá los sprites de Pokémon con fondo personalizado
        JPanel battlePanel = new BackgroundPanel(); // Usar el panel personalizado con fondo

        // Paneles de información de los Pokémon
        panelPok1 = new JPanel(new BorderLayout());
        panelPok1.setBackground(new Color(255, 255, 200)); // Amarillo claro Pokémon
        panelPok1.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        panelPok1.setBounds(20, 20, 250, 80); // Posición en la esquina superior izquierda

        labelInfo1 = new JLabel("", JLabel.CENTER);
        labelInfo1.setFont(pokemonFont.deriveFont(Font.BOLD, 14));

        hpBar1 = new JProgressBar(0, 100);
        hpBar1.setValue(100);
        hpBar1.setForeground(new Color(0, 200, 0));
        hpBar1.setStringPainted(true);
        hpBar1.setFont(pokemonFont.deriveFont(12f));
        hpBar1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        panelPok1.add(labelInfo1, BorderLayout.NORTH);
        panelPok1.add(hpBar1, BorderLayout.SOUTH);

        panelPok2 = new JPanel(new BorderLayout());
        panelPok2.setBackground(new Color(255, 255, 200)); // Amarillo claro Pokémon
        panelPok2.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        panelPok2.setBounds(530, 20, 250, 80); // Posición en la esquina superior derecha

        labelInfo2 = new JLabel("", JLabel.CENTER);
        labelInfo2.setFont(pokemonFont.deriveFont(Font.BOLD, 14));

        hpBar2 = new JProgressBar(0, 100);
        hpBar2.setValue(100);
        hpBar2.setForeground(new Color(0, 200, 0));
        hpBar2.setStringPainted(true);
        hpBar2.setFont(pokemonFont.deriveFont(12f));
        hpBar2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        panelPok2.add(labelInfo2, BorderLayout.NORTH);
        panelPok2.add(hpBar2, BorderLayout.SOUTH);

        // Etiquetas para los sprites de Pokémon con posicionamiento estilo Esmeralda
        pok1Label = new JLabel("", JLabel.CENTER);
        pok1Label.setBounds(80, 220, 200, 200); // Pokémon activo en la parte inferior izquierda

        pok2Label = new JLabel("", JLabel.CENTER);
        pok2Label.setBounds(500, 90, 200, 200); // Pokémon rival en la parte superior derecha

        // Agregar componentes al panel de batalla
        battlePanel.add(panelPok1);
        battlePanel.add(panelPok2);
        battlePanel.add(pok1Label);
        battlePanel.add(pok2Label);

        // Panel inferior con opciones
        panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(200, 224, 248)); // Azul claro Pokémon
        panelInferior.setBorder(BorderFactory.createLineBorder(new Color(64, 120, 192), 3));

        // Panel de información
        JPanel panelInfo = new JPanel();
        panelInfo.setPreferredSize(new Dimension(300, 100));
        panelInfo.setBackground(new Color(255, 255, 200)); // Amarillo claro Pokémon
        panelInfo.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        infoLabel = new JLabel("", JLabel.CENTER);
        infoLabel.setFont(pokemonFont);
        panelInfo.add(infoLabel);

        turnTimerLabel = new JLabel("Tiempo restante: 20s", JLabel.CENTER);
        turnTimerLabel.setFont(pokemonFont.deriveFont(Font.BOLD, 16));
        panelInfo.add(turnTimerLabel);

        // Panel de opciones principales
        cardLayout = new CardLayout();
        panelOpciones = new JPanel(cardLayout);
        panelOpciones.setPreferredSize(new Dimension(450, 110)); // Fijar tamaño del panel de opciones

        JPanel mainOptionsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        mainOptionsPanel.setBackground(new Color(200, 224, 248)); // Azul claro Pokémon

        btnAtacar = createBattleButton("ATACAR", new Color(200, 60, 60)); // Rojo Pokémon
        btnCambiar = createBattleButton("CAMBIAR", new Color(60, 140, 200)); // Azul Pokémon
        btnItem = createBattleButton("USAR ÍTEM", new Color(60, 200, 60)); // Verde Pokémon
        btnHuir = createBattleButton("HUIR", new Color(200, 160, 60)); // Amarillo Pokémon

        mainOptionsPanel.add(btnAtacar);
        mainOptionsPanel.add(btnCambiar);
        mainOptionsPanel.add(btnItem);
        mainOptionsPanel.add(btnHuir);

        JPanel attackOptionsPanel = new JPanel();
        attackOptionsPanel.setBackground(new Color(200, 224, 248)); // Azul claro Pokémon

        panelOpciones.add(mainOptionsPanel, "main");
        panelOpciones.add(attackOptionsPanel, "attacks");

        // Panel de registro de batalla
        logPanel = new BattleLogPanel();
        logPanel.setPreferredSize(new Dimension(getWidth(), 50));
        logPanel.setBackground(new Color(64, 120, 192)); // Azul oscuro Pokémon
        logPanel.setForeground(Color.WHITE);
        logPanel.setFont(pokemonFont.deriveFont(14f));

        // Añadir componentes al panel principal
        panelInferior.add(panelInfo, BorderLayout.WEST);
        panelInferior.add(panelOpciones, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(logPanel, BorderLayout.NORTH);
        bottomPanel.add(panelInferior, BorderLayout.SOUTH);

        mainPanel.add(battlePanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
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

    public void updateTurnTimer(int secondsLeft) {
        turnTimerLabel.setText("Tiempo restante: " + secondsLeft + "s");
    }

    private void prepareListeners() {
        btnAtacar.addActionListener(e -> controller.showAttackOptions());
        btnCambiar.addActionListener(e -> controller.showSwitchPokemonDialog());
        btnItem.addActionListener(e -> controller.showItemSelectionDialog());
        btnHuir.addActionListener(e -> controller.handleSurrender());
    }

    public void showAttackOptions(List<Move> moves) {
        JPanel attackPanel = (JPanel) panelOpciones.getComponent(1);
        attackPanel.removeAll();

        // Usar un GridLayout fijo con mismo tamaño que el panel principal
        int rows = Math.min(5, moves.size() + 1); // máximo 4 movimientos + botón cancelar
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

        // Asegurar que el panel mantiene un tamaño consistente
        attackPanel.setPreferredSize(new Dimension(450, 150));

        cardLayout.show(panelOpciones, "attacks");
        attackPanel.revalidate();
        attackPanel.repaint();
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    private void showModeDescription(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Descripción del Modo",
                JOptionPane.INFORMATION_MESSAGE);
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

    public void setGameMode(int mode) {
        this.gameMode = mode;
    }

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

    public BattleLogPanel getBattleLogPanel(){
        return logPanel;
    }
    private class BackgroundPanel extends JPanel {
        private BufferedImage backgroundImage;

        public BackgroundPanel() {
            setLayout(null); // Usamos posicionamiento absoluto para el estilo Esmeralda
            loadBackgroundImage();
        }

        private void loadBackgroundImage() {
            try {
                backgroundImage = ImageIO.read(new File("src/sprites/battleBackground.png"));
            } catch (IOException e) {
                System.err.println("Error al cargar la imagen de fondo: " + e.getMessage());
                backgroundImage = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // Escalar la imagen para que cubra todo el panel
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            } else {
                // Si no se puede cargar la imagen, usar el color de fondo por defecto
                g.setColor(new Color(120, 184, 232));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BattleGUI gui = new BattleGUI();
            gui.showInitialScreen();
        });
    }
}