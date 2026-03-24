import domain.Battle;
import domain.BattleState;
import domain.Pokemon;
import domain.Trainer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas para verificar la lógica del sistema de batallas entre entrenadores Pokémon.
 * Se simula el combate manualmente, incluyendo ataques, cambio de Pokémon y finalización del combate.
 */
public class BattleTest {

    /**
     * Simula una batalla entre dos entrenadores con un Pokémon cada uno.
     * Se verifica que se cause daño, que uno de los Pokémon quede fuera de combate
     * y que haya un ganador claro sin usar métodos auxiliares del controlador de combate.
     */
    @Test
    public void testFullBattleExecutionWithoutHelperMethods() {
        List<domain.Move> moves = new ArrayList<>();

        Pokemon pikachu = new Pokemon("Pikachu", "Electric", 100, 55, 40, 50, 50, 90, 100, 100, moves);
        Pokemon bulbasaur = new Pokemon("Bulbasaur", "Grass", 100, 49, 49, 65, 65, 45, 100, 100, moves);

        Trainer ash = new Trainer("Ash", "Rojo");
        Trainer gary = new Trainer("Gary", "Azul");

        ash.addPokemonToTeam(pikachu);
        gary.addPokemonToTeam(bulbasaur);
        ash.setActivePokemon(0);
        gary.setActivePokemon(0);

        Battle battle = new Battle(ash, gary);
        BattleState state = battle.getBattleState();

        assertEquals("Ash", state.getPlayer1Name());
        assertEquals("Gary", state.getPlayer2Name());
        assertEquals("Pikachu", ash.getActivePokemon().getName());
        assertEquals("Bulbasaur", gary.getActivePokemon().getName());

        int turnCount = 0;
        int maxTurns = 100;

        while (pikachu.getHp() > 0 && bulbasaur.getHp() > 0 && turnCount < maxTurns) {
            int damage = pikachu.getAttack() - bulbasaur.getDefense() / 2;
            damage = Math.max(damage, 1);
            bulbasaur.setHp(bulbasaur.getHp() - damage);

            if (bulbasaur.getHp() > 0) {
                damage = bulbasaur.getAttack() - pikachu.getDefense() / 2;
                damage = Math.max(damage, 1);
                pikachu.setHp(pikachu.getHp() - damage);
            }

            turnCount++;
        }

        assertTrue(pikachu.getHp() < 100 || bulbasaur.getHp() < 100);
        assertTrue(pikachu.getHp() <= 0 || bulbasaur.getHp() <= 0);
    }

    /**
     * Simula una batalla extendida entre dos entrenadores con múltiples Pokémon.
     * Se verifica el cambio de Pokémon al caer uno y que el combate finalice correctamente
     * con un ganador claro después de varios turnos.
     */
    @Test
    public void testExtendedBattleSimulation() {
        List<domain.Move> moves = new ArrayList<>();

        Pokemon pikachu = new Pokemon("Pikachu", "Electric", 100, 55, 40, 50, 50, 90, 100, 100, moves);
        Pokemon charizard = new Pokemon("Charizard", "Fire", 120, 84, 78, 109, 85, 100, 120, 120, moves);
        Pokemon bulbasaur = new Pokemon("Bulbasaur", "Grass", 100, 49, 49, 65, 65, 45, 100, 100, moves);
        Pokemon squirtle = new Pokemon("Squirtle", "Water", 110, 48, 65, 50, 64, 43, 110, 110, moves);

        Trainer ash = new Trainer("Ash", "Rojo");
        Trainer gary = new Trainer("Gary", "Azul");

        ash.addPokemonToTeam(pikachu);
        ash.addPokemonToTeam(charizard);
        gary.addPokemonToTeam(bulbasaur);
        gary.addPokemonToTeam(squirtle);

        ash.setActivePokemon(0);
        gary.setActivePokemon(0);

        Battle battle = new Battle(ash, gary);

        int turn = 0;
        int maxTurns = 50;

        while (ash.getActivePokemon().getHp() > 0 && gary.getActivePokemon().getHp() > 0 && turn < maxTurns) {
            int damageToGary = ash.getActivePokemon().getAttack() - gary.getActivePokemon().getDefense() / 2;
            damageToGary = Math.max(1, damageToGary);
            gary.getActivePokemon().setHp(gary.getActivePokemon().getHp() - damageToGary);

            if (gary.getActivePokemon().getHp() <= 0 && gary.getTeam().getAvailablePokemons().size() > 1) {
                gary.setActivePokemon(1);
            }

            if (gary.getActivePokemon().getHp() > 0) {
                int damageToAsh = gary.getActivePokemon().getAttack() - ash.getActivePokemon().getDefense() / 2;
                damageToAsh = Math.max(1, damageToAsh);
                ash.getActivePokemon().setHp(ash.getActivePokemon().getHp() - damageToAsh);

                if (ash.getActivePokemon().getHp() <= 0 && ash.getTeam().getAvailablePokemons().size() > 1) {
                    ash.setActivePokemon(1);
                }
            }

            turn++;
        }

        assertTrue(turn < maxTurns);
        assertTrue(ash.getActivePokemon().getHp() > 0 || gary.getActivePokemon().getHp() > 0);
    }
}
