package com.beyondsignal.game.combat.ai.planner;

import com.beyondsignal.game.combat.ai.goal.CombatAiGoal;
import com.beyondsignal.game.combat.ai.goal.CombatAiGoalType;
import com.beyondsignal.game.combat.command.CombatCommand;
import com.beyondsignal.game.combat.command.FireCombatWeaponCommand;
import com.beyondsignal.game.combat.command.SelectCombatTargetCommand;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CombatActionPlanner {
    public CombatAiPlan plan(
        DecisionContext context,
        CombatAiGoal goal,
        double engagementDistance
    ) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(goal, "goal");
        if (!Double.isFinite(engagementDistance) || engagementDistance < 0.0) {
            throw new IllegalArgumentException("engagementDistance must be non-negative");
        }

        List<CombatCommand> commands = new ArrayList<>();

        if (goal.type() == CombatAiGoalType.ACQUIRE_TARGET) {
            commands.add(new SelectCombatTargetCommand(
                context.snapshot().combatId(),
                context.self().participantId(),
                goal.targetId(),
                Instant.EPOCH
            ));
        }

        if (goal.type() == CombatAiGoalType.ATTACK_TARGET) {
            context.self().weapons().stream()
                .filter(weapon -> weapon.ready())
                .filter(weapon -> engagementDistance <= weapon.maximumRange())
                .sorted((left, right) -> {
                    int damage = Integer.compare(right.baseDamage(), left.baseDamage());
                    return damage != 0
                        ? damage
                        : left.mountId().compareTo(right.mountId());
                })
                .findFirst()
                .ifPresent(weapon -> commands.add(new FireCombatWeaponCommand(
                    context.snapshot().combatId(),
                    context.self().participantId(),
                    weapon.mountId(),
                    engagementDistance,
                    1.0,
                    Instant.EPOCH
                )));
        }

        return new CombatAiPlan(goal, commands);
    }
}
