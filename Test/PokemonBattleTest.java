import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

/**
 * Pruebas unitarias para las clases principales del sistema de batalla Pokémon:
 * {@code Pokemon}, {@code Trainer}, {@code Battle}, {@code Move}, {@code Item}, {@code Team}, {@code Action}, y estrategias de IA.
 */
class PokemonBattleTest {
    private Pokemon pikachu;
    private Pokemon charmander;
    private Trainer ash;
    private Trainer gary;
    private Battle battle;
    private CPUTrainer cpuTrainer;
    private Battle cpuBattle;

    /**
     * Configura el entorno antes de cada prueba.
     */
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

        cpuTrainer = new CPUTrainer("CPU", "Verde");
        cpuTrainer.addPokemonToTeam(pikachu.clone());
        cpuTrainer.setActivePokemon(0);
        cpuBattle = new Battle(ash, cpuTrainer);
    }

    /**
     * Verifica que un Pokémon reciba daño correctamente.
     */
    @Test
    void pokemonShouldTakeDamageWhenAttacked() {
        int initialHP = pikachu.getHp();
        pikachu.takeDamage(20);
        assertEquals(initialHP - 20, pikachu.getHp());
    }

    /**
     * Verifica que el HP de un Pokémon no sea negativo.
     */
    @Test
    void pokemonShouldNotHaveNegativeHP() {
        pikachu.takeDamage(200);
        assertEquals(0, pikachu.getHp());
    }

    /**
     * Verifica que un Pokémon pueda curarse hasta su HP máximo.
     */
    @Test
    void pokemonShouldHealUpToMaxHP() {
        pikachu.takeDamage(50);
        pikachu.heal(30);
        assertEquals(80, pikachu.getHp());

        pikachu.heal(100);
        assertEquals(pikachu.getMaxHp(), pikachu.getHp());
    }

    /**
     * Verifica que un Pokémon pueda revivir si está debilitado.
     */
    @Test
    void pokemonShouldReviveWhenFainted() {
        pikachu.takeDamage(pikachu.getHp());
        pikachu.revive(50);
        assertTrue(pikachu.getHp() > 0);
    }

    /**
     * Verifica que un Pokémon debilitado no pueda atacar.
     */
    @Test
    void pokemonShouldNotAttackWhenFainted() {
        Pokemon faintedPikachu = pikachu.clone();
        faintedPikachu.takeDamage(faintedPikachu.getHp());
        int initialHP = charmander.getHp();
        faintedPikachu.attack(0, charmander);
        assertEquals(initialHP, charmander.getHp());
    }

    /**
     * Verifica que un entrenador pueda cambiar a un Pokémon saludable.
     */
    @Test
    void trainerShouldBeAbleToSwitchPokemon() {
        Pokemon squirtle = new Pokemon("Squirtle", "WATER", 100, 48, 65, 50, 64, 43, 100, 100,
                Arrays.asList(MoveDatabase.getMove("WATER GUN")));
        ash.addPokemonToTeam(squirtle);

        ash.switchPokemon(1);
        assertEquals(squirtle, ash.getActivePokemon());
    }

    /**
     * Verifica que no se pueda cambiar a un Pokémon debilitado.
     */
    @Test
    void trainerShouldNotSwitchToFaintedPokemon() {
        Pokemon squirtle = new Pokemon("Squirtle", "WATER", 100, 48, 65, 50, 64, 43, 100, 100,
                Arrays.asList(MoveDatabase.getMove("WATER GUN")));
        squirtle.takeDamage(squirtle.getHp());
        ash.addPokemonToTeam(squirtle);

        ash.switchPokemon(1);
        assertNotEquals(squirtle, ash.getActivePokemon());
    }

    /**
     * Verifica el uso correcto de ítems por parte del entrenador.
     */
    @Test
    void trainerShouldUseItemsCorrectly() {
        ash.getItems().add(new Potion());
        int initialHP = pikachu.getHp();
        pikachu.takeDamage(30);
        ash.useItem(0, 0);
        assertTrue(pikachu.getHp() > initialHP - 30);
    }

    /**
     * Verifica que Revive no afecte a un Pokémon saludable.
     */
    @Test
    void trainerShouldNotUseReviveOnHealthyPokemon() {
        ash.getItems().add(new Revive());
        int initialHP = pikachu.getHp();
        ash.useItem(0, 0);
        assertEquals(initialHP, pikachu.getHp());
    }

    /**
     * Verifica que el primer turno de la batalla sea del jugador 1.
     */
    @Test
    void battleShouldStartWithPlayer1Turn() {
        assertEquals(ash, battle.getCurrentPlayer());
    }

    /**
     * Verifica que se apliquen correctamente los efectos del clima.
     */
    @Test
    void battleShouldApplyClimateEffects() {
        Battle.setClimate("RAIN", 5);
        assertEquals("RAIN", Battle.getClimate());
    }

    /**
     * Verifica que el clima expire después de los turnos especificados.
     */
    @Test
    void climateShouldExpireAfterTurns() {
        Battle.setClimate("SUNNY", 2);
        battle.performAction(Action.createAttack(0));
        battle.performAction(Action.createAttack(0));
        assertNull(Battle.getClimate());
    }

    /**
     * Verifica que la CPU pueda adoptar una estrategia defensiva.
     */
    @Test
    void cpuTrainerShouldSwitchToDefensiveStrategyWhenSet() {
        cpuTrainer.setStrategy(new DefensiveStrategy());
        assertNotNull(cpuTrainer.decideAction(cpuBattle));
    }

    /**
     * Verifica que la estrategia defensiva prefiera movimientos defensivos.
     */
    @Test
    void defensiveStrategyShouldPreferDefensiveMoves() {
        DefensiveStrategy strategy = new DefensiveStrategy();
        Pokemon attacker = pikachu.clone();
        attacker.getMoves().add(MoveDatabase.getMove("PROTECT"));
        Action action = strategy.decideAction(cpuTrainer, cpuBattle);
        assertNotNull(action);
    }

    /**
     * Verifica que los movimientos físicos calculen daño según el ataque.
     */
    @Test
    void physicalMoveShouldCalculateDamageBasedOnAttack() {
        PhysicalMove move = (PhysicalMove) MoveDatabase.getMove("AERIAL ACE");
        Pokemon attacker = new Pokemon("Test", "NORMAL", 100, 100, 50, 50, 50, 50, 100, 100, List.of(move));
        Pokemon defender = new Pokemon("Test", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of(move));
        int initialHP = defender.getHp();
        move.use(attacker, defender);
        assertTrue(defender.getHp() < initialHP);
    }

    /**
     * Verifica que los movimientos especiales usen el ataque especial para calcular daño.
     */
    @Test
    void specialMoveShouldCalculateDamageBasedOnSpecialAttack() {
        SpecialMove move = (SpecialMove) MoveDatabase.getMove("FLAMETHROWER");
        Pokemon attacker = new Pokemon("Test", "FIRE", 100, 50, 50, 100, 50, 50, 100, 100, List.of(move));
        Pokemon defender = new Pokemon("Test", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of(move));
        int initialHP = defender.getHp();
        move.use(attacker, defender);
        assertTrue(defender.getHp() < initialHP);
    }

    /**
     * Verifica que los movimientos respeten la precisión.
     */
    @Test
    void moveShouldRespectAccuracy() {
        SpecialMove inaccurateMove = new SpecialMove("TEST", "ELECTRIC", 90, 0, 10, 0);
        Pokemon attacker = pikachu.clone();
        Pokemon defender = charmander.clone();
        attacker.getMoves().set(0, inaccurateMove);
        int initialHP = defender.getHp();
        inaccurateMove.use(attacker, defender);
        assertEquals(initialHP, defender.getHp());
    }

    /**
     * Verifica que los movimientos consuman PP al usarse.
     */
    @Test
    void moveShouldDecreasePPWhenUsed() {
        Move move = MoveDatabase.getMove("THUNDERBOLT");
        int initialPP = move.pp();
        move.use(pikachu, charmander);
        assertEquals(initialPP - 1, move.pp());
    }

    /**
     * Verifica que se cree una acción de tipo ataque correctamente.
     */
    @Test
    void actionShouldCreateAttackTypeCorrectly() {
        Action action = Action.createAttack(0);
        assertEquals(Action.Type.ATTACK, action.getType());
        assertEquals(0, action.getMoveIndex());
    }

    /**
     * Verifica que se cree una acción para usar ítem correctamente.
     */
    @Test
    void actionShouldCreateUseItemTypeCorrectly() {
        Action action = Action.createUseItem(1, 0);
        assertEquals(Action.Type.USE_ITEM, action.getType());
        assertEquals(1, action.getItemIndex());
        assertEquals(0, action.getTargetIndex());
    }

    /**
     * Verifica que se cree una acción de cambio de Pokémon correctamente.
     */
    @Test
    void actionShouldCreateSwitchPokemonTypeCorrectly() {
        Action action = Action.createSwitchPokemon(2);
        assertEquals(Action.Type.SWITCH_POKEMON, action.getType());
        assertEquals(2, action.getTargetIndex());
    }

    /**
     * Verifica que no se permita un índice negativo de movimiento.
     */
    @Test
    void actionShouldNotAllowNegativeMoveIndex() {
        assertThrows(IllegalArgumentException.class, () -> Action.createAttack(-2));
    }

    /**
     * Verifica que una poción cure al Pokémon sin exceder el máximo.
     */
    @Test
    void potionShouldHealPokemonButNotExceedMaxHP() {
        Potion potion = new Potion();
        Pokemon pokemon = new Pokemon("Test", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        pokemon.takeDamage(30);
        potion.use(pokemon);
        assertEquals(90, pokemon.getHp());
    }

    /**
     * Verifica que una HyperPotion cure más que una Potion normal.
     */
    @Test
    void hyperPotionShouldHealMoreThanPotion() {
        HyperPotion hyperPotion = new HyperPotion();
        Pokemon pokemon = new Pokemon("Test", "NORMAL", 200, 50, 50, 50, 50, 50, 100, 100, List.of());
        pokemon.takeDamage(100);
        hyperPotion.use(pokemon);
        assertTrue(pokemon.getHp() > 100);
    }

    /**
     * Verifica que Revive no afecte a un Pokémon saludable.
     */
    @Test
    void reviveShouldNotWorkOnHealthyPokemon() {
        Revive revive = new Revive();
        Pokemon healthyPokemon = new Pokemon("Test", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        revive.use(healthyPokemon);
        assertEquals(100, healthyPokemon.getHp());
    }

    /**
     * Verifica que Revive restaure la mitad del HP a un Pokémon debilitado.
     */
    @Test
    void reviveShouldRestoreHalfHPToFaintedPokemon() {
        Revive revive = new Revive();
        Pokemon faintedPokemon = new Pokemon("Test", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        faintedPokemon.takeDamage(100);
        revive.use(faintedPokemon);
        assertEquals(50, faintedPokemon.getHp());
    }

    /**
     * Verifica que el equipo encuentre un Pokémon sano que no sea el activo.
     */
    @Test
    void teamShouldFindHealthyPokemonExcludingActive() {
        Team team = new Team();
        Pokemon active = new Pokemon("Active", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        Pokemon healthy = new Pokemon("Healthy", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        team.addPokemon(active);
        team.addPokemon(healthy);
        team.setActivePokemon(0);
        assertEquals(1, team.findHealthyPokemon());
    }

    /**
     * Verifica que no se encuentre ningún Pokémon sano si todos están debilitados.
     */
    @Test
    void teamShouldNotFindHealthyPokemonIfAllFainted() {
        Team team = new Team();
        Pokemon fainted1 = new Pokemon("Fainted1", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        Pokemon fainted2 = new Pokemon("Fainted2", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        fainted1.takeDamage(100);
        fainted2.takeDamage(100);
        team.addPokemon(fainted1);
        team.addPokemon(fainted2);
        assertEquals(-1, team.findHealthyPokemon());
    }

    /**
     * Verifica que se detecte correctamente cuando todos los Pokémon del equipo están debilitados.
     */
    @Test
    void teamShouldDetectAllFaintedPokemon() {
        Team team = new Team();
        Pokemon fainted = new Pokemon("Fainted", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        fainted.takeDamage(100);
        team.addPokemon(fainted);
        assertTrue(team.isAllFainted());
    }

    /**
     * Verifica que no se pueda cambiar al Pokémon activo si está debilitado.
     */
    @Test
    void teamShouldNotAllowSwitchingToFaintedPokemon() {
        Team team = new Team();
        Pokemon active = new Pokemon("Active", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        Pokemon fainted = new Pokemon("Fainted", "NORMAL", 100, 50, 50, 50, 50, 50, 100, 100, List.of());
        fainted.takeDamage(100);
        team.addPokemon(active);
        team.addPokemon(fainted);
        team.switchPokemon(1);
        assertNotEquals(fainted, team.getActivePokemon());
    }
}
