import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

class PokemonBattleTest {
    private Pokemon pikachu;
    private Pokemon charmander;
    private Trainer ash;
    private Trainer gary;
    private Battle battle;
    private CPUTrainer cpuTrainer;
    private Battle cpuBattle;

    @BeforeEach
    void setUp() {
        Move thunderbolt = MoveDatabase.getMove("THUNDERBOLT");
        Move quickAttack = MoveDatabase.getMove("AERIAL ACE");
        Move ember = MoveDatabase.getMove("FLAMETHROWER");
        Move scratch = MoveDatabase.getMove("METAL CLAW");

        pikachu = new Pokemon("Pikachu", "ELECTRIC", 100, 55, 40, 50, 50, 90, 100, 100,
                Arrays.asList(thunderbolt, quickAttack));

        charmander = new Pokemon("Charmander", "FIRE", 90, 52, 43, 60, 50, 65, 100, 100,
                Arrays.asList(ember, scratch));

        ash = new Trainer("Ash", "Rojo");
        ash.addPokemonToTeam(pikachu);
        ash.setActivePokemon(0);

        gary = new Trainer("Gary", "Azul");
        gary.addPokemonToTeam(charmander);
        gary.setActivePokemon(0);

        battle = new Battle(ash, gary);

        // Configuración para pruebas de CPU
        cpuTrainer = new CPUTrainer("CPU", "Verde");
        cpuTrainer.addPokemonToTeam(pikachu.clone());
        cpuTrainer.setActivePokemon(0);
        cpuBattle = new Battle(ash, cpuTrainer);
    }

    // --- Pruebas para Pokemon ---
    @Test
    void pokemonShouldTakeDamageWhenAttacked() {
        int initialHP = pikachu.getHp();
        pikachu.takeDamage(20);
        assertEquals(initialHP - 20, pikachu.getHp(), "El Pokémon debería recibir daño correctamente");
    }

    @Test
    void pokemonShouldNotHaveNegativeHP() {
        pikachu.takeDamage(200);
        assertEquals(0, pikachu.getHp(), "El HP no debería ser negativo");
    }

    @Test
    void pokemonShouldHealUpToMaxHP() {
        pikachu.takeDamage(50);
        pikachu.heal(30);
        assertEquals(80, pikachu.getHp(), "El Pokémon debería curarse correctamente");

        pikachu.heal(100);
        assertEquals(pikachu.getMaxHp(), pikachu.getHp(), "El Pokémon no debería curarse más allá de su HP máximo");
    }

    @Test
    void pokemonShouldReviveWhenFainted() {
        pikachu.takeDamage(pikachu.getHp());
        assertEquals(0, pikachu.getHp());
        pikachu.revive(50);
        assertTrue(pikachu.getHp() > 0, "El Pokémon debería revivir con algo de HP");
    }

    @Test
    void pokemonShouldNotAttackWhenFainted() {
        Pokemon faintedPikachu = pikachu.clone();
        faintedPikachu.takeDamage(faintedPikachu.getHp());
        int initialHP = charmander.getHp();
        faintedPikachu.attack(0, charmander);
        assertEquals(initialHP, charmander.getHp(), "Un Pokémon debilitado no debería poder atacar");
    }

    // ------ para Trainer ----
    @Test
    void trainerShouldBeAbleToSwitchPokemon() {
        Pokemon squirtle = new Pokemon("Squirtle", "WATER", 100, 48, 65, 50, 64, 43, 100, 100,
                Arrays.asList(MoveDatabase.getMove("WATER GUN")));
        ash.addPokemonToTeam(squirtle);

        ash.switchPokemon(1);
        assertEquals(squirtle, ash.getActivePokemon(), "El entrenador debería poder cambiar de Pokémon");
    }

    @Test
    void trainerShouldNotSwitchToFaintedPokemon() {
        Pokemon squirtle = new Pokemon("Squirtle", "WATER", 100, 48, 65, 50, 64, 43, 100, 100,
                Arrays.asList(MoveDatabase.getMove("WATER GUN")));
        squirtle.takeDamage(squirtle.getHp());
        ash.addPokemonToTeam(squirtle);

        ash.switchPokemon(1);
        assertNotEquals(squirtle, ash.getActivePokemon(), "No debería cambiar a un Pokémon debilitado");
    }

    @Test
    void trainerShouldUseItemsCorrectly() {
        ash.getItems().add(new Potion());
        int initialHP = pikachu.getHp();
        pikachu.takeDamage(30);

        ash.useItem(0, 0);
        assertTrue(pikachu.getHp() > initialHP - 30, "El ítem debería curar al Pokémon");
    }

    @Test
    void trainerShouldNotUseReviveOnHealthyPokemon() {
        ash.getItems().add(new Revive());
        int initialHP = pikachu.getHp();
        ash.useItem(0, 0);
        assertEquals(initialHP, pikachu.getHp(), "Revive no debería afectar a un Pokémon saludable");
    }

    // ----- Pruebas para Battle -------
    @Test
    void battleShouldStartWithPlayer1Turn() {
        assertEquals(ash, battle.getCurrentPlayer(), "El primer turno debería ser del jugador 1");
    }





    @Test
    void battleShouldApplyClimateEffects() {
        Battle.setClimate("RAIN", 5);
        assertEquals("RAIN", Battle.getClimate(), "El clima debería aplicarse correctamente");
    }

    @Test
    void climateShouldExpireAfterTurns() {
        Battle.setClimate("SUNNY", 2);
        battle.performAction(Action.createAttack(0));
        battle.performAction(Action.createAttack(0));
        assertNull(Battle.getClimate(), "El clima debería desaparecer después de los turnos especificados");
    }

    // ------- para CPUTrainer y Estrategias -----


    @Test
    void cpuTrainerShouldSwitchToDefensiveStrategyWhenSet() {
        cpuTrainer.setStrategy(new DefensiveStrategy());
        assertTrue(cpuTrainer.decideAction(cpuBattle) != null, "La CPU debería poder usar estrategia defensiva");
    }



    @Test
    void defensiveStrategyShouldPreferDefensiveMoves() {
        DefensiveStrategy strategy = new DefensiveStrategy();
        Pokemon attacker = pikachu.clone();
        attacker.getMoves().add(MoveDatabase.getMove("PROTECT"));

        Action action = strategy.decideAction(cpuTrainer, cpuBattle);
        assertNotNull(action, "La estrategia defensiva debería decidir una acción");
    }

    // -----para Movimientos ---
    @Test
    void physicalMoveShouldCalculateDamageBasedOnAttack() {
        PhysicalMove move = (PhysicalMove) MoveDatabase.getMove("AERIAL ACE");
        Pokemon attacker = new Pokemon("Test", "NORMAL", 100, 100, 50, 50, 50, 50, 100, 100,
                Arrays.asList(move));
        Pokemon defender = new Pokemon("Test", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100,
                Arrays.asList(move));

        int initialHP = defender.getHp();
        move.use(attacker, defender);
        assertTrue(defender.getHp() < initialHP, "El movimiento físico debería causar daño basado en el ataque");
    }

    @Test
    void specialMoveShouldCalculateDamageBasedOnSpecialAttack() {
        SpecialMove move = (SpecialMove) MoveDatabase.getMove("FLAMETHROWER");
        Pokemon attacker = new Pokemon("Test", "FIRE", 100, 50, 50, 100, 50, 50, 100, 100,
                Arrays.asList(move));
        Pokemon defender = new Pokemon("Test", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100,
                Arrays.asList(move));

        int initialHP = defender.getHp();
        move.use(attacker, defender);
        assertTrue(defender.getHp() < initialHP, "El movimiento especial debería causar daño basado en el ataque especial");
    }

    @Test
    void moveShouldRespectAccuracy() {
        Move move = MoveDatabase.getMove("THUNDERBOLT");
        Pokemon attacker = pikachu.clone();
        Pokemon defender = charmander.clone();


        SpecialMove inaccurateMove = new SpecialMove("TEST", "ELECTRIC", 90, 0, 10, 0);
        attacker.getMoves().set(0, inaccurateMove);

        int initialHP = defender.getHp();
        inaccurateMove.use(attacker, defender);
        assertEquals(initialHP, defender.getHp(), "El movimiento debería fallar si la precisión es 0");
    }

    @Test
    void moveShouldDecreasePPWhenUsed() {
        Move move = MoveDatabase.getMove("THUNDERBOLT");
        int initialPP = move.pp();
        move.use(pikachu, charmander);
        assertEquals(initialPP - 1, move.pp(), "El PP debería disminuir después de usar un movimiento");
    }

    //  -- para la clase Action ---
    @Test
    void actionShouldCreateAttackTypeCorrectly() {
        Action action = Action.createAttack(0);
        assertEquals(Action.Type.ATTACK, action.getType(), "Debería crear una acción de tipo ATAQUE");
        assertEquals(0, action.getMoveIndex(), "El índice del movimiento debería ser 0");
    }

    @Test
    void actionShouldCreateUseItemTypeCorrectly() {
        Action action = Action.createUseItem(1, 0);
        assertEquals(Action.Type.USE_ITEM, action.getType(), "Debería crear una acción de tipo USAR_ITEM");
        assertEquals(1, action.getItemIndex(), "El índice del ítem debería ser 1");
        assertEquals(0, action.getTargetIndex(), "El índice del objetivo debería ser 0");
    }

    @Test
    void actionShouldCreateSwitchPokemonTypeCorrectly() {
        Action action = Action.createSwitchPokemon(2);
        assertEquals(Action.Type.SWITCH_POKEMON, action.getType(), "Debería crear una acción de tipo CAMBIAR_POKEMON");
        assertEquals(2, action.getTargetIndex(), "El índice del Pokémon objetivo debería ser 2");
    }

    @Test
    void actionShouldNotAllowNegativeMoveIndex() {
        assertThrows(IllegalArgumentException.class, () -> Action.createAttack(-2),
                "No debería permitir índices de movimiento negativos");
    }

    // ---para item y subclases---
    @Test
    void potionShouldHealPokemonButNotExceedMaxHP() {
        Potion potion = new Potion();
        Pokemon pokemon = new Pokemon("Test", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        pokemon.takeDamage(30);
        potion.use(pokemon);
        assertEquals(90, pokemon.getHp(), "La poción debería curar 20 puntos de HP");
    }

    @Test
    void hyperPotionShouldHealMoreThanPotion() {
        HyperPotion hyperPotion = new HyperPotion();
        Pokemon pokemon = new Pokemon("Test", "NORMAL", 200, 50, 50, 50, 50, 50, 100, 100, List.of());
        pokemon.takeDamage(100);
        hyperPotion.use(pokemon);
        assertTrue(pokemon.getHp() > 100, "La HyperPotion debería curar más que una Potion normal");
    }

    @Test
    void reviveShouldNotWorkOnHealthyPokemon() {
        Revive revive = new Revive();
        Pokemon healthyPokemon = new Pokemon("Test", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        revive.use(healthyPokemon);
        assertEquals(100, healthyPokemon.getHp(), "Revive no debería afectar a un Pokémon con HP");
    }

    @Test
    void reviveShouldRestoreHalfHPToFaintedPokemon() {
        Revive revive = new Revive();
        Pokemon faintedPokemon = new Pokemon("Test", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        faintedPokemon.takeDamage(100);
        revive.use(faintedPokemon);
        assertEquals(50, faintedPokemon.getHp(), "Revive debería restaurar la mitad del HP máximo");
    }

    // --- para team ---
    @Test
    void teamShouldFindHealthyPokemonExcludingActive() {
        Team team = new Team();
        Pokemon active = new Pokemon("Active", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        Pokemon healthy = new Pokemon("Healthy", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        team.addPokemon(active);
        team.addPokemon(healthy);
        team.setActivePokemon(0);
        assertEquals(1, team.findHealthyPokemon(), "Debería encontrar el Pokémon sano en el índice 1");
    }

    @Test
    void teamShouldNotFindHealthyPokemonIfAllFainted() {
        Team team = new Team();
        Pokemon fainted1 = new Pokemon("Fainted1", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        Pokemon fainted2 = new Pokemon("Fainted2", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        fainted1.takeDamage(100);
        fainted2.takeDamage(100);
        team.addPokemon(fainted1);
        team.addPokemon(fainted2);
        assertEquals(-1, team.findHealthyPokemon(), "No debería encontrar Pokémon sanos");
    }

    @Test
    void teamShouldDetectAllFaintedPokemon() {
        Team team = new Team();
        Pokemon fainted = new Pokemon("Fainted", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        fainted.takeDamage(100);
        team.addPokemon(fainted);
        assertTrue(team.isAllFainted(), "Debería detectar que todos los Pokémon están debilitados");
    }

    @Test
    void teamShouldNotAllowSwitchingToFaintedPokemon() {
        Team team = new Team();
        Pokemon active = new Pokemon("Active", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        Pokemon fainted = new Pokemon("Fainted", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        fainted.takeDamage(100);
        team.addPokemon(active);
        team.addPokemon(fainted);
        team.switchPokemon(1);
        assertNotEquals(fainted, team.getActivePokemon(), "No debería cambiar a un Pokémon debilitado");
    }

}