package domain;

import presentation.BattleGUI;
import presentation.ItemSelectionGUI;
import presentation.MoveSelectionGUI;
import presentation.PokemonSelectionGUI;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class GameController implements Serializable {
    private Battle currentBattle;
    private BattleGUI gui;
    private Timer turnTimer;
    private int remainingSeconds;
    public static final int MODO_NORMAL = 0;
    public static final int MODO_SUPERVIVENCIA = 1;
    private static final long serialVersionUID = 1L;
    private BattleEventListener eventListener;

    /**
     * Crea un nuevo controlador del juego con la interfaz de usuario dada.
     *
     * @param gui la interfaz gráfica de la batalla
     */
    public GameController(BattleGUI gui) {
        this.gui = gui;
        this.eventListener = gui;
    }

    /**
     * Inicia el juego con el modo y los nombres de los jugadores especificados.
     * Configura la selección de Pokémon, ítems y estrategias según el modo de juego.
     *
     * @param gameMode    el modo de juego (1 = PvP, 2 = PvM, 3 = MvM)
     * @param player1Name nombre del primer jugador
     * @param player2Name nombre del segundo jugador
     */
    public void startGame(int gameMode, String player1Name, String player2Name) {
        Trainer player1 = gameMode == 3 ? new CPUTrainer("CPU Ash", "Rojo") : new Trainer(player1Name, "Rojo");
        Trainer player2 = gameMode == 1 ? new Trainer(player2Name, "Azul") : new CPUTrainer("CPU Gary", "Azul");

        showPokemonSelection(player1, () -> {
            showItemSelection(player1, () -> {
                if (gameMode == 1 || gameMode == 3) {
                    showPokemonSelection(player2, () -> {
                        showItemSelection(player2, () -> {
                            if (gameMode == 3) {
                                selectCPUStrategy((CPUTrainer) player1, () -> {
                                    selectCPUStrategy((CPUTrainer) player2, () -> {
                                        startBattle(player1, player2);
                                    });
                                });
                            } else {
                                startBattle(player1, player2);
                            }
                        });
                    });
                } else {
                    selectCPUStrategy((CPUTrainer) player2, () -> {
                        showPokemonSelection(player2, () -> {
                            showItemSelection(player2, () -> {
                                startBattle(player1, player2);
                            });
                        });
                    });
                }
            });
        });
    }

    /**
     * Permite seleccionar la estrategia de combate de un entrenador CPU.
     *
     * @param cpu        el entrenador CPU
     * @param onComplete acción a ejecutar tras la selección de estrategia
     */
    private void selectCPUStrategy(CPUTrainer cpu, Runnable onComplete) {
        String[] options = {"Defensivo", "Ofensivo", "Cambiador", "Experto"};
        String selected = (String) JOptionPane.showInputDialog(
                gui,
                "Selecciona la estrategia para " + cpu.getName() + ":",
                "Estrategia de CPU",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (selected != null) {
            switch (selected) {
                case "Defensivo":
                    cpu.setStrategy(new DefensiveStrategy());
                    break;
                case "Ofensivo":
                    cpu.setStrategy(new AttackingStrategy());
                    break;
                case "Cambiador":
                    cpu.setStrategy(new ChangingStrategy());
                    break;
                case "Experto":
                    cpu.setStrategy(new ExpertStrategy());
                    break;
            }
        }
        onComplete.run();
    }

    /**
     * Inicia una nueva batalla entre dos entrenadores.
     * Si ambos son CPU, se activa la batalla automática.
     *
     * @param player1 el primer entrenador
     * @param player2 el segundo entrenador
     */
    private void startBattle(Trainer player1, Trainer player2) {
        if (!player1.getTeam().getPokemons().isEmpty()) {
            player1.setActivePokemon(0);
        }
        if (!player2.getTeam().getPokemons().isEmpty()) {
            player2.setActivePokemon(0);
        }

        this.currentBattle = new Battle(player1, player2);
        gui.setupBattleWindow();
        updateUI();

        if (player1.isCPU() && player2.isCPU()) {
            startAutoBattle();
        }
    }

    /**
     * Actualiza la interfaz gráfica con la información actual de la batalla.
     * Este método es público para que pueda ser invocado desde la GUI.
     */
    public void updateUI() {
        if (currentBattle != null) {
            gui.updateBattleInfo(currentBattle.getBattleState());
            startTurnTimer();

            if (gui.isAttackPanelVisible()) {
                showAttackOptions();
            }
        }
    }

    /**
     * Inicia un temporizador para limitar el tiempo del turno del jugador.
     * Si se agota el tiempo, se aplica una penalización y se finaliza el turno.
     */
    private void startTurnTimer() {
        if (!currentBattle.getCurrentPlayer().isCPU()) {
            if (turnTimer != null) {
                turnTimer.stop();
            }

            remainingSeconds = 20;
            gui.updateTurnTimer(remainingSeconds);

            turnTimer = new Timer(1000, e -> {
                remainingSeconds--;
                gui.updateTurnTimer(remainingSeconds);
                if (remainingSeconds <= 0) {
                    turnTimer.stop();
                    applyTurnTimeoutPenalty();
                    endPlayerTurn();
                }
            });
            turnTimer.start();
        }
    }

    /**
     * Aplica una penalización al jugador actual por exceder el tiempo del turno.
     * Todos los movimientos especiales pierden 1 punto de poder (PP).
     */
    private void applyTurnTimeoutPenalty() {
        Trainer current = currentBattle.getCurrentPlayer();
        Pokemon p = current.getActivePokemon();

        for (Move move : p.getMoves()) {
            if (move instanceof SpecialMove && move.pp() > 0) {
                ((SpecialMove) move).setPP(move.pp() - 1);
            }
        }

        JOptionPane.showMessageDialog(gui,
                current.getName() + " se tardó demasiado. ¡Todos los movimientos especiales pierden 1 PP!");
    }

    /**
     * Muestra un cuadro de diálogo para configurar los nombres de los jugadores según el modo de juego.
     *
     * @param gameMode modo de juego seleccionado (1 = PvP, 2 = PvM, 3 = MvM)
     */
    public void showPlayerSetup(int gameMode) {
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
                gui,
                setupPanel,
                "Configuración de jugadores",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String player1Name = gameMode == 3 ? "CPU Ash" :
                    (player1Field.getText().trim().isEmpty() ? "Jugador 1" : player1Field.getText());
            String player2Name = gameMode == 3 ? "CPU Gary" :
                    (player2Field.getText().trim().isEmpty() ? "Jugador 2" : player2Field.getText());

            startGame(gameMode, player1Name, player2Name);
        }
    }

    /**
     * Muestra las opciones de ataque disponibles para el jugador actual.
     */
    public void showAttackOptions() {
        Trainer current = currentBattle.getCurrentPlayer();
        List<Move> moves = current.getActivePokemon().getMoves();
        gui.showAttackOptions(moves);
    }

    /**
     * Ejecuta el ataque seleccionado por el jugador y actualiza la interfaz.
     *
     * @param moveIndex índice del movimiento seleccionado
     */
    public void executeAttack(int moveIndex) {
        gui.getBattleLogPanel().clearMessages();
        Pokemon attacker = currentBattle.getCurrentPlayer().getActivePokemon();
        Pokemon target = currentBattle.getOpponent().getActivePokemon();
        Move move = attacker.getMoves().get(moveIndex);
        // Notificar el evento de ataque
        if (eventListener != null) {
            eventListener.onAttackPerformed(attacker.getName(), target.getName(), move.name());
        }

        currentBattle.performAction(Action.createAttack(moveIndex));
        updateUI();

        if (!currentBattle.isFinished()) {
            if (turnTimer != null) {
                turnTimer.stop();
            }

            endPlayerTurn();
        } else {
            checkBattleEnd();
        }
    }

    /**
     * Muestra un cuadro de diálogo para cambiar el Pokémon activo del jugador actual.
     */
    public void showSwitchPokemonDialog() {
        gui.getBattleLogPanel().clearMessages();
        Trainer current = currentBattle.getCurrentPlayer();

        JDialog switchDialog = new JDialog(gui, "Cambiar Pokémon", true);
        switchDialog.setLayout(new BorderLayout());
        switchDialog.setSize(450, 300);
        switchDialog.setLocationRelativeTo(gui);
        switchDialog.getContentPane().setBackground(new Color(152, 251, 152)); // Verde Esmeralda

        JLabel titleLabel = new JLabel("Selecciona un Pokémon", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        switchDialog.add(titleLabel, BorderLayout.NORTH);

        JPanel pokePanel = new JPanel(new GridLayout(0, 1, 5, 5));
        pokePanel.setBackground(new Color(152, 251, 152));

        for (int i = 0; i < current.getTeam().getPokemons().size(); i++) {
            Pokemon p = current.getTeam().getPokemons().get(i);
            String pokeName = p.getName();
            int hp = p.getHp();
            int maxHp = p.getMaxHp();
            boolean isFainted = (hp <= 0);

            String label = pokeName + " (HP: " + hp + "/" + maxHp + ")";
            if (isFainted) label += " - Debilitado";

            JButton pokeButton = new JButton(label);

            // Cargar imagen
            String path = "src/sprites/" + pokeName.toLowerCase() + "_front.png";
            File imageFile = new File(path);
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(path);
                Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                pokeButton.setIcon(new ImageIcon(scaled));
            }

            pokeButton.setHorizontalAlignment(SwingConstants.LEFT);
            pokeButton.setBackground(isFainted ? Color.LIGHT_GRAY : new Color(176, 224, 230));
            pokeButton.setEnabled(!isFainted);
            pokeButton.setFocusPainted(false);

            int index = i;
            pokeButton.addActionListener(e -> {
                switchDialog.dispose();

                // Notificar evento
                if (eventListener != null) {
                    eventListener.onPokemonSwitched(current.getName(), pokeName);
                }

                currentBattle.performAction(Action.createSwitchPokemon(index));
                updateUI();
                if (turnTimer != null) {
                    turnTimer.stop();
                }
                endPlayerTurn();
            });

            pokePanel.add(pokeButton);
        }

        JScrollPane scrollPane = new JScrollPane(pokePanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        switchDialog.add(scrollPane, BorderLayout.CENTER);
        switchDialog.setVisible(true);
    }

    /**
     * Muestra un cuadro de diálogo para que el jugador actual seleccione un ítem y un objetivo.
     * Si hay ítems disponibles, permite usarlos sobre un Pokémon del equipo.
     */
    public void showItemSelectionDialog() {
        gui.getBattleLogPanel().clearMessages();
        Trainer current = currentBattle.getCurrentPlayer();

        if (current.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(gui, "No tienes ítems disponibles.");
            return;
        }

        JDialog itemDialog = new JDialog(gui, "Usar Ítem", true);
        itemDialog.setLayout(new BorderLayout());
        itemDialog.setSize(400, 300);
        itemDialog.setLocationRelativeTo(gui);
        itemDialog.getContentPane().setBackground(new Color(152, 251, 152)); // Verde Esmeralda

        JLabel titleLabel = new JLabel("Selecciona un ítem", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        itemDialog.add(titleLabel, BorderLayout.NORTH);

        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new GridLayout(0, 1, 5, 5));
        itemPanel.setBackground(new Color(152, 251, 152));

        for (int i = 0; i < current.getItems().size(); i++) {
            Item item = current.getItems().get(i);
            String itemName = item.getName();
            JButton itemButton = new JButton(itemName);

            // Cargar imagen del ítem
            String imagePath = "src/sprites/" + itemName.toLowerCase() + ".png";
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(imagePath);
                Image scaled = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                itemButton.setIcon(new ImageIcon(scaled));
            }

            itemButton.setBackground(new Color(176, 224, 230));
            itemButton.setFocusPainted(false);
            itemButton.setHorizontalAlignment(SwingConstants.LEFT);

            int itemIndex = i;
            itemButton.addActionListener(e -> {
                itemDialog.dispose();

                // Determinar si el ítem es un revive
                boolean isReviveItem = isReviveItem(item);

                // Segunda ventana: selección de Pokémon objetivo
                JDialog targetDialog = new JDialog(gui, "Objetivo del Ítem", true);
                targetDialog.setLayout(new BorderLayout());
                targetDialog.setSize(450, 300);
                targetDialog.setLocationRelativeTo(gui);
                targetDialog.getContentPane().setBackground(new Color(152, 251, 152));

                JLabel targetLabel = new JLabel("Selecciona un Pokémon objetivo", SwingConstants.CENTER);
                targetLabel.setFont(new Font("Arial", Font.BOLD, 16));
                targetLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                targetDialog.add(targetLabel, BorderLayout.NORTH);

                JPanel pokePanel = new JPanel(new GridLayout(0, 1, 5, 5));
                pokePanel.setBackground(new Color(152, 251, 152));

                for (int j = 0; j < current.getTeam().getPokemons().size(); j++) {
                    Pokemon p = current.getTeam().getPokemons().get(j);
                    String pokeName = p.getName();
                    int hp = p.getHp();
                    int maxHp = p.getMaxHp();
                    boolean isFainted = (hp <= 0);

                    String label = pokeName + " (HP: " + hp + "/" + maxHp + ")";
                    if (isFainted) label += " - Debilitado";

                    JButton pokeButton = new JButton(label);

                    // Imagen del Pokémon
                    String pokeImagePath = "src/sprites/" + pokeName.toLowerCase() + "_front.png";
                    File pokeImageFile = new File(pokeImagePath);
                    if (pokeImageFile.exists()) {
                        ImageIcon icon = new ImageIcon(pokeImagePath);
                        Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                        pokeButton.setIcon(new ImageIcon(scaled));
                    }

                    pokeButton.setHorizontalAlignment(SwingConstants.LEFT);

                    // Lógica corregida para habilitar/deshabilitar botones según el tipo de ítem
                    boolean canUseItem;
                    if (isReviveItem) {
                        // Para revive: solo pokémon debilitados
                        canUseItem = isFainted;
                        pokeButton.setBackground(isFainted ? new Color(176, 224, 230) : Color.LIGHT_GRAY);
                    } else {
                        // Para otros ítems (pociones, etc.): solo pokémon no debilitados
                        canUseItem = !isFainted;
                        pokeButton.setBackground(isFainted ? Color.LIGHT_GRAY : new Color(176, 224, 230));
                    }

                    pokeButton.setEnabled(canUseItem);
                    pokeButton.setFocusPainted(false);

                    int targetIndex = j;
                    pokeButton.addActionListener(ev -> {
                        targetDialog.dispose();

                        Item selectedItem = current.getItems().get(itemIndex);
                        Pokemon targetPokemon = current.getTeam().getPokemons().get(targetIndex);

                        if (eventListener != null) {
                            eventListener.onItemUsed(current.getName(), selectedItem.getName(), targetPokemon.getName());
                        }

                        currentBattle.performAction(Action.createUseItem(itemIndex, targetIndex));
                        updateUI();
                        if (turnTimer != null) {
                            turnTimer.stop();
                        }
                        endPlayerTurn();
                    });

                    pokePanel.add(pokeButton);
                }

                JScrollPane scrollPane = new JScrollPane(pokePanel);
                scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                targetDialog.add(scrollPane, BorderLayout.CENTER);
                targetDialog.setVisible(true);
            });

            itemPanel.add(itemButton);
        }

        JScrollPane scrollPane = new JScrollPane(itemPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        itemDialog.add(scrollPane, BorderLayout.CENTER);
        itemDialog.setVisible(true);
    }

    private boolean isReviveItem(Item item) {
        String itemName = item.getName().toLowerCase();
        return itemName.contains("revive") || itemName.contains("revivir") ||
                itemName.contains("revival") || itemName.equals("max revive");
    }



    /**
     * Muestra un cuadro de diálogo para que el jugador actual confirme si desea rendirse.
     * Si acepta, se muestra el mensaje de derrota.
     */
    public void handleSurrender() {
        int option = JOptionPane.showConfirmDialog(
                gui,
                "¿Quieres rendirte?",
                "Rendición",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            gui.showBattleEnd("Te has rendido. ¡Perdiste la batalla!");
        }
    }

    /**
     * Finaliza el turno del jugador actual, cambia el turno y actualiza la interfaz.
     * Si el siguiente jugador es CPU, ejecuta automáticamente su turno.
     */
    private void endPlayerTurn() {
        if (currentBattle.isFinished()) {
            checkBattleEnd();
            return;
        }

        currentBattle.changeTurn();
        updateUI();

        gui.showMainOptions();

        if (!currentBattle.isFinished() && currentBattle.getCurrentPlayer().isCPU()) {
            executeCpuTurn();
        }
    }

    /**
     * Ejecuta el turno del jugador CPU de forma automatizada y recursiva si sigue siendo CPU.
     * Se utiliza un pequeño retardo entre turnos para simular tiempo de juego.
     */
    private void executeCpuTurn() {
        SwingUtilities.invokeLater(() -> {
            if (!currentBattle.isFinished()) {
                currentBattle.executeCpuTurn();
                updateUI();

                if (!currentBattle.isFinished()) {
                    currentBattle.changeTurn();
                    updateUI();

                    if (currentBattle.getCurrentPlayer().isCPU()) {

                        Timer timer = new Timer(1000, e -> executeCpuTurn());
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        gui.showMainOptions();
                    }
                } else {
                    checkBattleEnd();
                }
            }
        });
    }

    /**
     * Inicia una batalla automática completa entre dos entrenadores CPU.
     * El combate continúa de forma automatizada hasta que finalice.
     */
    private void startAutoBattle() {
        Timer timer = new Timer(1500, e -> {
            if (!currentBattle.isFinished()) {
                currentBattle.executeCpuTurn();
                updateUI();

                if (currentBattle.getCurrentPlayer().isCPU()) {
                    currentBattle.changeTurn();
                    updateUI();
                }
            } else {
                checkBattleEnd();
            }
        });
        timer.start();
    }

    /**
     * Verifica si la batalla ha terminado y muestra el resultado correspondiente.
     */
    private void checkBattleEnd() {
        if (currentBattle.isFinished()) {
            Trainer winner = currentBattle.getWinner();
            String message = winner != null ?
                    "¡" + winner.getName() + " ha ganado la batalla!" :
                    "¡La batalla ha terminado en empate!";
            gui.showBattleEnd(message);
        }
    }

    /**
     * Muestra la interfaz de selección de Pokémon para un entrenador.
     * Luego de seleccionar, se procede a seleccionar los movimientos de cada Pokémon.
     *
     * @param trainer    el entrenador que selecciona Pokémon
     * @param onComplete acción a ejecutar al finalizar la selección
     */
    private void showPokemonSelection(Trainer trainer, Runnable onComplete) {
        PokemonSelectionGUI selectionGUI = new PokemonSelectionGUI(gui, trainer, 6, selectedPokemons -> {
            selectMovesForPokemons(selectedPokemons, onComplete);
        });
        selectionGUI.setVisible(true);
    }

    /**
     * Inicia el proceso de selección de movimientos para cada Pokémon del equipo.
     * El proceso es recursivo hasta que todos los Pokémon hayan seleccionado sus movimientos.
     *
     * @param pokemons   lista de Pokémon a configurar
     * @param onComplete acción a ejecutar al finalizar la selección
     */
    private void selectMovesForPokemons(List<Pokemon> pokemons, Runnable onComplete) {
        if (pokemons.isEmpty()) {
            onComplete.run();
            return;
        }
        Pokemon current = pokemons.get(0);
        MoveSelectionGUI moveGUI = new MoveSelectionGUI(gui, current);
        moveGUI.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                selectMovesForPokemons(pokemons.subList(1, pokemons.size()), onComplete);
            }
        });
        moveGUI.setVisible(true);
    }



    /**
     * Asigna movimientos aleatorios a un Pokémon.
     *
     * @param pokemon el Pokémon al que se le asignarán los movimientos
     */
    private void selectRandomMoves(Pokemon pokemon) {
        List<Move> allMoves = MoveDatabase.getAvailableMoves();
        Collections.shuffle(allMoves);
        pokemon.setMoves(allMoves.subList(0, Math.min(4, allMoves.size())));
    }

    /**
     * Muestra la interfaz de selección de ítems para un entrenador.
     *
     * @param trainer    el entrenador que selecciona los ítems
     * @param onComplete acción a ejecutar al finalizar
     */
    private void showItemSelection(Trainer trainer, Runnable onComplete) {
        SwingUtilities.invokeLater(() -> {
            ItemSelectionGUI.showItemSelection(gui, trainer);
            onComplete.run();
        });
    }


    /**
     * Asigna ítems aleatorios al entrenador CPU.
     *
     * @param cpu        entrenador CPU
     * @param onComplete acción a ejecutar al finalizar
     */
    private void selectItemsForCPU(Trainer cpu, Runnable onComplete) {
        List<Item> items = Arrays.asList(
                new Potion(),
                new SuperPotion(),
                new HyperPotion(),
                new Revive()
        );
        Collections.shuffle(items);
        cpu.getItems().clear();
        cpu.getItems().addAll(items.subList(0, Math.min(3, items.size())));
        onComplete.run();
    }

    /**
     * Retorna la batalla actual en curso.
     *
     * @return objeto de tipo Battle que representa la batalla actual
     */
    public Battle getCurrentBattle() {
        return currentBattle;
    }

    /**
     * Inicia una partida en modo supervivencia con equipos aleatorios.
     *
     * @param player1Name nombre del jugador 1
     * @param player2Name nombre del jugador 2
     */
    public void startSurvivalMode(String player1Name, String player2Name) {
        Trainer player1 = new Trainer(player1Name, "Rojo");
        Trainer player2 = new Trainer(player2Name, "Azul");

        assignRandomTeam(player1);
        assignRandomTeam(player2);

        if (!player1.getTeam().getPokemons().isEmpty()) {
            player1.setActivePokemon(0);
        }
        if (!player2.getTeam().getPokemons().isEmpty()) {
            player2.setActivePokemon(0);
        }

        this.currentBattle = new Battle(player1, player2);

        updateUI();
    }

    /**
     * Asigna un equipo aleatorio de 6 Pokémon con movimientos aleatorios a un entrenador.
     *
     * @param trainer entrenador al que se le asignará el equipo
     */
    private void assignRandomTeam(Trainer trainer) {
        for (int i = 0; i < 6; i++) {
            Pokemon random = PokemonDataBase.getRandomPokemon();
            random.setMoves(MoveDatabase.getRandomMoves(4));
            trainer.addPokemonToTeam(random);
        }
    }

    /**
     * Carga el estado guardado de una partida, incluyendo la batalla y el modo de juego.
     *
     * @param gameState estado del juego a cargar
     */
    public void loadGameState(GameState gameState) {
        this.currentBattle = gameState.getBattle();
        this.gui.setGameMode(gameState.getGameMode());
    }

    public Trainer getCurrentTrainer(){
    return currentBattle.getCurrentPlayer();
    }
}