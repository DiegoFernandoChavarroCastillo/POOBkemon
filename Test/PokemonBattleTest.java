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

    @BeforeEach
    void setUp() {
        // Crear Pokémon básicos para las pruebas usando MoveDatabase
        Move thunderbolt = MoveDatabase.getMove("THUNDERBOLT");
        Move quickAttack = MoveDatabase.getMove("AERIAL ACE"); // Movimiento físico
        Move ember = MoveDatabase.getMove("FLAMETHROWER");
        Move scratch = MoveDatabase.getMove("METAL CLAW");

        pikachu = new Pokemon("Pikachu", "ELECTRIC", 100, 55, 40, 50, 50, 90, 100, 100,
                Arrays.asList(thunderbolt, quickAttack));

        charmander = new Pokemon("Charmander", "FIRE", 90, 52, 43, 60, 50, 65, 100, 100,
                Arrays.asList(ember, scratch));

        // Crear entrenadores
        ash = new Trainer("Ash", "Rojo");
        ash.addPokemonToTeam(pikachu);
        ash.setActivePokemon(0);

        gary = new Trainer("Gary", "Azul");
        gary.addPokemonToTeam(charmander);
        gary.setActivePokemon(0);

        // Crear batalla
        battle = new Battle(ash, gary);
    }

    // Pruebas para la clase Pokemon
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





    // Pruebas para movimientos específicos
    @Test
    void thunderboltShouldBeElectricType() {
        Move thunderbolt = MoveDatabase.getMove("THUNDERBOLT");
        assertEquals("ELECTRIC", thunderbolt.type(), "Thunderbolt debería ser de tipo ELECTRIC");
    }

    @Test
    void flamethrowerShouldBeSpecialMove() {
        Move flamethrower = MoveDatabase.getMove("FLAMETHROWER");
        assertTrue(flamethrower instanceof SpecialMove, "Flamethrower debería ser un movimiento especial");
    }

    @Test
    void aerialAceShouldBePhysicalMove() {
        Move aerialAce = MoveDatabase.getMove("AERIAL ACE");
        assertTrue(aerialAce instanceof PhysicalMove, "Aerial Ace debería ser un movimiento físico");
    }

    // Pruebas para la clase Trainer
    @Test
    void trainerShouldBeAbleToSwitchPokemon() {
        Pokemon squirtle = new Pokemon("Squirtle", "WATER", 100, 48, 65, 50, 64, 43, 100, 100,
                Arrays.asList(MoveDatabase.getMove("WATER GUN")));
        ash.addPokemonToTeam(squirtle);

        ash.switchPokemon(1);
        assertEquals(squirtle, ash.getActivePokemon(), "El entrenador debería poder cambiar de Pokémon");
    }



    @Test
    void trainerShouldUseItemsCorrectly() {
        ash.getItems().add(new Potion());
        int initialHP = pikachu.getHp();
        pikachu.takeDamage(30);

        ash.useItem(0, 0); // Usar poción en el primer Pokémon
        assertTrue(pikachu.getHp() > initialHP - 30, "El ítem debería curar al Pokémon");
    }

    // Pruebas para la clase Battle
    @Test
    void battleShouldStartWithPlayer1Turn() {
        assertEquals(ash, battle.getCurrentPlayer(), "El primer turno debería ser del jugador 1");
    }





    // Pruebas para los ítems
    @Test
    void potionShouldHealPokemon() {
        int initialHP = pikachu.getHp();
        pikachu.takeDamage(30);
        new Potion().use(pikachu);
        assertTrue(pikachu.getHp() > initialHP - 30, "La poción debería curar al Pokémon");
    }

    @Test
    void reviveShouldNotWorkOnHealthyPokemon() {
        int initialHP = pikachu.getHp();
        new Revive().use(pikachu);
        assertEquals(initialHP, pikachu.getHp(), "Revive no debería afectar a un Pokémon saludable");
    }

    @Test
    void reviveShouldWorkOnFaintedPokemon() {
        pikachu.takeDamage(pikachu.getHp());
        new Revive().use(pikachu);
        assertTrue(pikachu.getHp() > 0, "Revive debería revivir a un Pokémon debilitado");
    }

    // Pruebas específicas para MoveDatabase
    @Test
    void moveDatabaseShouldReturnValidMove() {
        Move move = MoveDatabase.getMove("THUNDERBOLT");
        assertNotNull(move, "Debería devolver un movimiento válido");
        assertEquals("THUNDERBOLT", move.name(), "Debería devolver el movimiento solicitado");
    }

    @Test
    void moveDatabaseShouldReturnNullForInvalidMove() {
        Move move = MoveDatabase.getMove("INVALID_MOVE");
        assertNull(move, "Debería devolver null para movimientos no existentes");
    }

    @Test
    void moveDatabaseShouldReturnClonedMoves() {
        Move move1 = MoveDatabase.getMove("THUNDERBOLT");
        Move move2 = MoveDatabase.getMove("THUNDERBOLT");
        assertNotSame(move1, move2, "Debería devolver clones diferentes del mismo movimiento");
    }
}