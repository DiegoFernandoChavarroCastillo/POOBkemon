package domain;

import presentation.BattleGUI;
import presentation.ItemSelectionGUI;
import presentation.MoveSelectionGUI;
import presentation.PokemonSelectionGUI;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GameController {
    private Battle currentBattle;
    private BattleGUI gui;
    private Timer turnTimer;
    private int remainingSeconds;
    public static final int MODO_NORMAL = 0;
    public static final int MODO_SUPERVIVENCIA = 1;


    public GameController(BattleGUI gui) {
        this.gui = gui;
    }

    public void startGame(int gameMode, String player1Name, String player2Name) {
        Trainer player1 = gameMode == 3 ? new CPUTrainer("CPU Ash", "Rojo") : new Trainer(player1Name, "Rojo");
        Trainer player2 = gameMode == 1 ? new Trainer(player2Name, "Azul") : new CPUTrainer("CPU Gary", "Azul");

        showPokemonSelection(player1, () -> {
            showItemSelection(player1, () -> {
                // Modo PvP (1) o MvM (3) con selección completa
                if (gameMode == 1 || gameMode == 3) {
                    showPokemonSelection(player2, () -> {
                        showItemSelection(player2, () -> {
                            if (gameMode == 3) { // Solo en MvM seleccionamos estrategias
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
                }
                // Modo PvM (2) con selección de CPU
                else {
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

    // Nuevo método para seleccionar estrategia de CPU
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

    public void updateUI() {
        gui.updateBattleInfo(currentBattle.getBattleState());
        startTurnTimer();

        // Si el panel de ataques está visible, actualizarlo
        if (gui.isAttackPanelVisible()) {
            showAttackOptions();
        }
    }
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

    public void showAttackOptions() {
        Trainer current = currentBattle.getCurrentPlayer();
        List<Move> moves = current.getActivePokemon().getMoves();
        gui.showAttackOptions(moves);
    }

    public void executeAttack(int moveIndex) {
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

    public void showSwitchPokemonDialog() {
        Trainer current = currentBattle.getCurrentPlayer();
        String[] pokemons = new String[current.getTeam().getPokemons().size()];

        for (int i = 0; i < pokemons.length; i++) {
            Pokemon p = current.getTeam().getPokemons().get(i);
            String status = (p.getHp() <= 0) ? " - Debilitado" : "";
            pokemons[i] = p.getName() + " (HP: " + p.getHp() + "/" + p.getMaxHp() + ")" + status;
        }

        String selected = (String) JOptionPane.showInputDialog(
                gui,
                "Selecciona un Pokémon:",
                "Cambiar Pokémon",
                JOptionPane.PLAIN_MESSAGE,
                null,
                pokemons,
                pokemons[0]);

        if (selected != null) {
            int pokemonIndex = Arrays.asList(pokemons).indexOf(selected);
            currentBattle.performAction(Action.createSwitchPokemon(pokemonIndex));
            updateUI();
            if (turnTimer != null) {
                turnTimer.stop();
            }
            endPlayerTurn();
        }
    }

    public void showItemSelectionDialog() {
        Trainer current = currentBattle.getCurrentPlayer();

        if (current.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(gui, "No tienes ítems disponibles.");
            return;
        }

        String[] itemNames = current.getItems().stream()
                .map(Item::getName)
                .toArray(String[]::new);

        String selectedItemName = (String) JOptionPane.showInputDialog(
                gui,
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
                    gui,
                    "Selecciona un Pokémon objetivo:",
                    "Objetivo del Ítem",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    targets,
                    targets[0]);

            if (selectedTarget != null) {
                int targetIndex = Arrays.asList(targets).indexOf(selectedTarget);
                currentBattle.performAction(Action.createUseItem(itemIndex, targetIndex));
                updateUI();
                if (turnTimer != null) {
                    turnTimer.stop();
                }
                endPlayerTurn();
            }
        }
    }

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

    private void executeCpuTurn() {
        Timer timer = new Timer(1000, e -> {
            if (!currentBattle.isFinished()) {
                currentBattle.executeCpuTurn();
                updateUI();

                if (!currentBattle.isFinished()) {
                    currentBattle.changeTurn();
                    updateUI();

                    if (currentBattle.getCurrentPlayer().isCPU()) {
                        // Ejecutar siguiente turno de CPU con un pequeño delay
                        new Timer(1000, ev -> executeCpuTurn()).start();
                    } else {
                        gui.showMainOptions();
                    }
                } else {
                    checkBattleEnd();
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

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

    private void checkBattleEnd() {
        if (currentBattle.isFinished()) {
            Trainer winner = currentBattle.getWinner();
            String message = winner != null ?
                    "¡" + winner.getName() + " ha ganado la batalla!" :
                    "¡La batalla ha terminado en empate!";
            gui.showBattleEnd(message);
        }
    }


    private void showPokemonSelection(Trainer trainer, Runnable onComplete) {
        PokemonSelectionGUI selectionGUI = new PokemonSelectionGUI(gui, trainer, 6, selectedPokemons -> {
            selectMovesForPokemons(selectedPokemons, onComplete);
        });
        selectionGUI.setVisible(true);
    }

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

    private void selectPokemonForCPU(Trainer cpu, Runnable onComplete) {
        List<String> available = new ArrayList<>(PokemonDataBase.getAvailablePokemonNames());
        Collections.shuffle(available);

        int count = Math.min(6, available.size());
        for (int i = 0; i < count; i++) {
            Pokemon pokemon = PokemonDataBase.getPokemon(available.get(i));
            selectRandomMoves(pokemon);
            cpu.addPokemonToTeam(pokemon);
        }
        onComplete.run();
    }

    private void selectRandomMoves(Pokemon pokemon) {
        List<Move> allMoves = MoveDatabase.getAvailableMoves();
        Collections.shuffle(allMoves);
        pokemon.setMoves(allMoves.subList(0, Math.min(4, allMoves.size())));
    }

    private void showItemSelection(Trainer trainer, Runnable onComplete) {
        SwingUtilities.invokeLater(() -> {
            ItemSelectionGUI.showItemSelection(gui, trainer);
            onComplete.run();
        });
    }

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

    public Battle getCurrentBattle() {
        return currentBattle;
    }
    public void startSurvivalMode(String player1Name, String player2Name) {
        Trainer player1 = new Trainer(player1Name, "Rojo");
        Trainer player2 = new Trainer(player2Name, "Azul");

        assignRandomTeam(player1);
        assignRandomTeam(player2);

        this.currentBattle = new Battle(player1, player2); // principio de inversión de dependencias
    }

    private void assignRandomTeam(Trainer trainer) {
        for (int i = 0; i < 6; i++) {
            Pokemon random = PokemonDataBase.getRandomPokemon(); // debes implementar esto
            random.setMoves(MoveDatabase.getRandomMoves(4)); // también debes implementar esto
            trainer.addPokemonToTeam(random);
        }
    }
}