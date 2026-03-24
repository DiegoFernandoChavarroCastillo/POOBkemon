import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class StatusMoveTest {
    private Pokemon user;
    private Pokemon target;

    @BeforeEach
    public void setUp() {
        user = new Pokemon("Usermon", "Normal", 100, 50, 50, 50, 50, 50, 100, 100, new ArrayList<>());
        target = new Pokemon("Targetmon", "Normal", 100, 50, 50, 50, 50, 50, 100, 100, new ArrayList<>());
    }

    @Test
    public void shouldApplyTailWhipAndDecreaseDefense() {
        Move tailWhip = new StatusMove("TAIL WHIP", "NORMAL", 100, 30, 0,
                new Effect(EffectType.DEBUFF, Target.OPPONENT,
                        Map.of("defense", -1), null, 999, true, false)
        );

        tailWhip.use(user, target);
        int boostedDefense = target.getEffectiveStat("defense");
        assertEquals(49, boostedDefense); // 50 base - 1
    }

    @Test
    public void shouldApplyStringShotAndDecreaseSpeed() {
        Move stringShot = new StatusMove("STRING SHOT", "BUG", 95, 40, 0,
                new Effect(EffectType.DEBUFF, Target.OPPONENT,
                        Map.of("speed", -1), null, 999, true, false)
        );

        stringShot.use(user, target);
        int boostedSpeed = target.getEffectiveStat("speed");
        assertEquals(49, boostedSpeed); // 50 base - 1
    }

    @Test
    public void shouldApplyBulkUpAndIncreaseAttackAndDefense() {
        Move bulkUp = new StatusMove("BULK UP", "FIGHTING", 100, 20, 0,
                new Effect(EffectType.BUFF, Target.USER,
                        Map.of("attack", 1, "defense", 1), null, 999, true, false)
        );

        bulkUp.use(user, target);
        assertEquals(51, user.getEffectiveStat("attack"));
        assertEquals(51, user.getEffectiveStat("defense"));
    }

    @Test
    public void shouldApplyIronDefenseAndIncreaseDefenseByTwo() {
        Move ironDefense = new StatusMove("IRON DEFENSE", "STEEL", 100, 15, 0,
                new Effect(EffectType.BUFF, Target.USER,
                        Map.of("defense", 2), null, 999, true, false)
        );

        ironDefense.use(user, target);
        assertEquals(52, user.getEffectiveStat("defense"));
    }

    @Test
    public void shouldResetBoostsWithHaze() {
        // First apply some boosts
        user.modifyStat("attack", 2);
        target.modifyStat("defense", -1);

        Move haze = new StatusMove("HAZE", "ICE", 100, 30, 0,
                new Effect(EffectType.RESET_STATS, Target.USER, null, null, 1, false, false)
        );

        haze.use(user, target);

        assertEquals(50, user.getEffectiveStat("attack"));
        assertEquals(50, target.getEffectiveStat("defense"));
    }

    @Test
    public void shouldApplyToxicAndDealIncreasingDamage() {
        Move toxic = new StatusMove("TOXIC", "POISON", 90, 10, 0,
                new Effect(EffectType.STATUS, Target.OPPONENT, null, "toxic", 999, false, false)
        );

        toxic.use(user, target);
        int initialHp = target.getHp();
        target.processStartOfTurnEffects();
        int afterTurn1 = target.getHp();
        target.processStartOfTurnEffects();
        int afterTurn2 = target.getHp();

        assertTrue(afterTurn1 < initialHp);
        assertTrue(afterTurn2 < afterTurn1);
    }

    @Test
    public void shouldApplyBurnStatusAndDealDamageEachTurn() {
        Move willOWisp = new StatusMove("WILL-O-WISP", "FIRE", 85, 15, 0,
                new Effect(EffectType.STATUS, Target.OPPONENT, null, "burned", 999, false, false)
        );

        willOWisp.use(user, target);
        int hpBefore = target.getHp();
        target.processStartOfTurnEffects();
        int hpAfter = target.getHp();

        assertTrue(hpAfter < hpBefore);
    }

    @Test
    public void shouldApplyCurseAsNonGhostAndModifyStats() {
        user = new Pokemon("Normalmon", "Normal", 100, 50, 50, 50, 50, 50, 100, 100, new ArrayList<>());
        Move curse = new CurseMove();

        curse.use(user, target);

        assertEquals(51, user.getEffectiveStat("attack"));
        assertEquals(51, user.getEffectiveStat("defense"));
        assertEquals(49, user.getEffectiveStat("speed"));
    }

    @Test
    public void shouldApplyCurseAsGhostAndDamageUserAndCurseTarget() {
        user = new Pokemon("Ghastly", "Ghost", 100, 50, 50, 50, 50, 50, 100, 100, new ArrayList<>());
        Move curse = new CurseMove();

        curse.use(user, target);
        assertTrue(user.getHp() < 100);
        assertEquals("cursed", target.getStatus());

        int hpBefore = target.getHp();
        target.processStartOfTurnEffects();
        int hpAfter = target.getHp();
        assertTrue(hpAfter < hpBefore);
    }
}
