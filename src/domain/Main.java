package domain;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("¡Bienvenido a la batalla Pokémon!");
        System.out.println("\nSelecciona el modo de juego:");
        System.out.println("1. Jugador vs Jugador (PvP)");
        System.out.println("2. Jugador vs Máquina (PvM)");
        System.out.println("3. Máquina vs Máquina (MvM)");
        System.out.print("Elige una opción (1-3): ");

        int gameMode = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        // Configuración común para todos los modos
        Move tackle = getValidMove("ICE BEAM");
        Move flamethrower = getValidMove("HYDRO PUMP");
        Move thunderbolt = getValidMove("DRAGON CLAW");
        Move hydroPump = getValidMove("THUNDERBOLT");

        Pokemon charizard = PokemonDataBase.getPokemon("Charizard");
        charizard.setMoves(Arrays.asList(tackle, flamethrower));

        Pokemon blastoise = PokemonDataBase.getPokemon("Blastoise");
        blastoise.setMoves(Arrays.asList(tackle, hydroPump));

        Pokemon venusaur = PokemonDataBase.getPokemon("Charizard");
        venusaur.setMoves(Arrays.asList(tackle, thunderbolt));

        Trainer player1 = null;
        Trainer player2 = null;

        // Configuración específica por modo
        switch (gameMode) {
            case 1: // PvP
                System.out.print("\nNombre del Jugador 1: ");
                String name1 = scanner.nextLine();
                player1 = new Trainer(name1, "Rojo");
                player1.addPokemonToTeam(charizard);
                player1.addPokemonToTeam(venusaur);

                System.out.print("Nombre del Jugador 2: ");
                String name2 = scanner.nextLine();
                player2 = new Trainer(name2, "Azul");
                player2.addPokemonToTeam(blastoise);
                break;

            case 2: // PvM
                System.out.print("\nNombre del Jugador: ");
                String playerName = scanner.nextLine();
                player1 = new Trainer(playerName, "Rojo");
                player1.addPokemonToTeam(charizard);
                player1.addPokemonToTeam(venusaur);

                player2 = new CPUTrainer("CPU Gary", "Azul");
                player2.addPokemonToTeam(blastoise);
                break;

            case 3: // MvM
                player1 = new CPUTrainer("CPU Ash", "Rojo");
                player1.addPokemonToTeam(charizard);
                player1.addPokemonToTeam(venusaur);

                player2 = new CPUTrainer("CPU Gary", "Azul");
                player2.addPokemonToTeam(blastoise);
                break;

            default:
                System.out.println("Opción inválida. Saliendo...");
                System.exit(0);
        }

        // Iniciar batalla
        Battle battle = new Battle(player1, player2);
        startBattle(battle, scanner, gameMode);

        scanner.close();
    }

    private static void startBattle(Battle battle, Scanner scanner, int gameMode) {
        System.out.println("\n¡Comienza la batalla!");

        while (!battle.isFinished()) {
            Trainer current = battle.getCurrentPlayer();

            if (current.isCPU()) {
                System.out.println("\nTurno de " + current.getName() + "...");
                battle.executeCpuTurn();
            } else {
                // Mostrar estado
                System.out.println("\n" + battle.getBattleStatus());

                // Menú para jugador humano
                System.out.println("\nOpciones:");
                System.out.println("1. Atacar");
                System.out.println("2. Cambiar Pokémon");
                System.out.println("3. Usar ítem");
                System.out.print("Elige una opción: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer

                switch (choice) {
                    case 1: // Atacar
                        handleAttack(battle, current, scanner);
                        break;

                    case 2: // Cambiar Pokémon
                        handleSwitch(battle, current, scanner);
                        break;

                    case 3: // Usar ítem
                        handleItem(battle, current, scanner);
                        break;

                    default:
                        System.out.println("Opción inválida. Pierdes tu turno.");
                        battle.changeTurn();
                }
            }

            // Pausa para MvM
            if (gameMode == 3) {
                try {
                    Thread.sleep(1500); // Pausa más larga para MvM
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // Mostrar resultado final
        Trainer winner = battle.getWinner();
        if (winner != null) {
            System.out.printf("\n¡%s ha ganado la batalla!%n", winner.getName());
        } else {
            System.out.println("\n¡La batalla terminó en empate!");
        }
    }

    private static void handleAttack(Battle battle, Trainer current, Scanner scanner) {
        Pokemon currentPokemon = current.getActivePokemon();
        System.out.println("\nMovimientos disponibles:");
        for (int i = 0; i < currentPokemon.getMoves().size(); i++) {
            Move move = currentPokemon.getMoves().get(i);
            System.out.printf("%d. %s (PP: %d/%d)%n",
                    i, move.name(), move.pp(), move.maxPP());
        }
        System.out.print("Elige un movimiento: ");
        int moveIndex = scanner.nextInt();
        battle.performAction(Action.createAttack(moveIndex));
    }

    private static void handleSwitch(Battle battle, Trainer current, Scanner scanner) {
        List<Pokemon> team = current.getTeam().getPokemons();
        System.out.println("\nPokémon disponibles:");
        for (int i = 0; i < team.size(); i++) {
            Pokemon p = team.get(i);
            String status = p.getHp() <= 0 ? " (Debilitado)" : "";
            System.out.printf("%d. %s [HP: %d/%d]%s%n",
                    i, p.getName(), p.getHp(), p.getMaxHp(), status);
        }
        System.out.print("Elige un Pokémon: ");
        int pokemonIndex = scanner.nextInt();
        battle.performAction(Action.createSwitchPokemon(pokemonIndex));
    }

    private static void handleItem(Battle battle, Trainer current, Scanner scanner) {
        if (current.getItems().isEmpty()) {
            System.out.println("No tienes ítems disponibles.");
            return;
        }
        System.out.println("\nÍtems disponibles:");
        for (int i = 0; i < current.getItems().size(); i++) {
            System.out.printf("%d. %s%n", i, current.getItems().get(i).getClass().getSimpleName());
        }
        System.out.print("Elige un ítem: ");
        int itemIndex = scanner.nextInt();
        System.out.println("\nPokémon objetivos:");
        for (int i = 0; i < current.getTeam().getPokemons().size(); i++) {
            Pokemon p = current.getTeam().getPokemons().get(i);
            System.out.printf("%d. %s [HP: %d/%d]%n",
                    i, p.getName(), p.getHp(), p.getMaxHp());
        }
        System.out.print("Elige un objetivo: ");
        int targetIndex = scanner.nextInt();
        battle.performAction(Action.createUseItem(itemIndex, targetIndex));
    }

    private static Move getValidMove(String moveName) {
        Move move = MoveDatabase.getMove(moveName);
        if (move == null) {
            System.err.println("¡Advertencia! Movimiento " + moveName + " no encontrado. Usando Struggle como respaldo.");
            return new Struggle();
        }
        return move;
    }
}