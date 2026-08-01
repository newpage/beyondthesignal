package com.beyondsignal.game.combat.ai.planner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import com.beyondsignal.game.combat.ai.evaluation.ThreatTable;
import com.beyondsignal.game.combat.ai.goal.CombatAiGoal;
import com.beyondsignal.game.combat.ai.goal.CombatAiGoalType;
import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.CombatantSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.ShieldSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.WeaponSnapshot;
import com.beyondsignal.game.combat.command.FireCombatWeaponCommand;
import com.beyondsignal.game.combat.command.SelectCombatTargetCommand;
import com.beyondsignal.game.combat.engine.CombatParticipantStatus;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldQuadrant;
import com.beyondsignal.game.combat.weapon.WeaponType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatActionPlannerTest {
    @Test
    void generatesTargetSelectionCommand() {
        UUID selfId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        DecisionContext context = context(selfId, null, List.of());

        CombatAiPlan plan = new CombatActionPlanner().plan(
            context,
            new CombatAiGoal(
                CombatAiGoalType.ACQUIRE_TARGET,
                targetId,
                10,
                Map.of("threat", 10)
            ),
            1000.0
        );

        assertEquals(1, plan.commands().size());
        assertInstanceOf(SelectCombatTargetCommand.class, plan.commands().getFirst());
    }

    @Test
    void choosesHighestDamageReadyWeapon() {
        UUID selfId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        UUID weakMount = UUID.randomUUID();
        UUID strongMount = UUID.randomUUID();

        DecisionContext context = context(
            selfId,
            targetId,
            List.of(
                weapon(weakMount, 10),
                weapon(strongMount, 40)
            )
        );

        CombatAiPlan plan = new CombatActionPlanner().plan(
            context,
            new CombatAiGoal(
                CombatAiGoalType.ATTACK_TARGET,
                targetId,
                10,
                Map.of("threat", 10)
            ),
            1000.0
        );

        FireCombatWeaponCommand command = assertInstanceOf(
            FireCombatWeaponCommand.class,
            plan.commands().getFirst()
        );
        assertEquals(strongMount, command.weaponMountId());
    }

    private static DecisionContext context(
        UUID selfId,
        UUID targetId,
        List<WeaponSnapshot> weapons
    ) {
        EnumMap<ShieldQuadrant, Integer> values = new EnumMap<>(ShieldQuadrant.class);
        for (ShieldQuadrant quadrant : ShieldQuadrant.values()) {
            values.put(quadrant, 50);
        }
        CombatantSnapshot self = new CombatantSnapshot(
            selfId,
            CombatSide.FRIENDLY,
            CombatParticipantStatus.ACTIVE,
            100,
            100,
            new ShieldSnapshot(values, values),
            weapons,
            targetId,
            0.8,
            0.2
        );
        CombatAiSnapshot snapshot = new CombatAiSnapshot(
            CombatId.random(),
            1L,
            List.of(self)
        );
        return new DecisionContext(
            snapshot,
            self,
            new ThreatTable(selfId, List.of())
        );
    }

    private static WeaponSnapshot weapon(UUID mountId, int damage) {
        return new WeaponSnapshot(
            mountId,
            "weapon-" + damage,
            WeaponType.BEAM,
            true,
            0,
            0,
            damage,
            10000.0
        );
    }
}
