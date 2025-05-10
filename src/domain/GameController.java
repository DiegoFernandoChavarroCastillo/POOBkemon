package domain;

import presentation.BattleGUI;
import presentation.ItemSelectionGUI;
import presentation.MoveSelectionGUI;
import presentation.PokemonSelectionGUI;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.*;
import java.util.List;

import static presentation.ItemSelectionGUI.showItemSelection;

public class GameController {
    private Battle currentBattle;
    private BattleGUI gui;
    private int gameMode;

    public GameController() {

    }

    public void startGame(int gameMode, String player1Name, String player2Name) {
        this.gameMode = gameMode;


        Trainer player1 = gameMode == 3 ? new CPUTrainer("CPU Ash", "Rojo") : new Trainer(player1Name, "Rojo");
        Trainer player2 = gameMode == 1 ? new Trainer(player2Name, "Azul") : new CPUTrainer("CPU Gary", "Azul");


        showPokemonSelection(player1, () -> {

            showItemSelection(player1, () -> {
                if (gameMode == 1) {
                    showPokemonSelection(player2, () -> {
                        showItemSelection(player2, () -> startBattle(player1, player2));
                    });
                } else {
                    selectPokemonForCPU(player2, () -> {
                        selectItemsForCPU(player2, () -> startBattle(player1, player2));
                    });
                }
            });
        });
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
        moveGUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
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

    private void startBattle(Trainer player1, Trainer player2) {

        if (!player1.getTeam().getPokemons().isEmpty()) {
            player1.setActivePokemon(0);
        } else {
            System.err.println("Error: Player 1 has no Pokémon in team!");
        }

        if (!player2.getTeam().getPokemons().isEmpty()) {
            player2.setActivePokemon(0); // Establece el primer Pokémon como activo
        } else {
            System.err.println("Error: Player 2 has no Pokémon in team!");
        }

        this.currentBattle = new Battle(player1, player2);
        gui.setBattle(currentBattle);
        gui.updateBattleInfo();

        if (gameMode == 3) {
            startAutoBattle();
        }
    }

    private void startAutoBattle() {
        new Thread(() -> {
            while (!currentBattle.isFinished()) {
                try {
                    Thread.sleep(1500);
                    SwingUtilities.invokeLater(() -> {
                        currentBattle.executeCpuTurn();
                        gui.updateBattleInfo();
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public void setGUI(BattleGUI gui) {
        this.gui = gui;
    }

    public Battle getCurrentBattle() {
        return currentBattle;
    }

    private static Move getValidMove(String moveName) {
        Move move = MoveDatabase.getMove(moveName);
        if (move == null) {
            System.err.println("¡Advertencia! Movimiento " + moveName + " no encontrado. Usando Struggle como respaldo.");
            return new Struggle();
        }
        return move;
    }





    public abstract class WindowAdapter implements WindowListener {
        public void windowOpened(WindowEvent e) {}
        public void windowClosing(WindowEvent e) {}
        public void windowClosed(WindowEvent e) {}
        public void windowIconified(WindowEvent e) {}
        public void windowDeiconified(WindowEvent e) {}
        public void windowActivated(WindowEvent e) {}
        public void windowDeactivated(WindowEvent e) {}
    }
}