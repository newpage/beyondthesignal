package com.beyondsignal.game.combat.fleet.squadron;

import com.beyondsignal.game.combat.command.CombatCommand;
import com.beyondsignal.game.combat.command.SelectCombatTargetCommand;
import com.beyondsignal.game.combat.engine.CombatEncounter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SquadronOrderExecutor {
    public List<CombatCommand> commands(
        CombatEncounter encounter,
        SquadronExecutionPlan plan
    ) {
        Objects.requireNonNull(encounter, "encounter");
        Objects.requireNonNull(plan, "plan");

        if (plan.targetId() == null
            || plan.mode() == SquadronExecutionMode.HOLD
            || plan.mode() == SquadronExecutionMode.WITHDRAW) {
            return List.of();
        }

        List<CombatCommand> commands = new ArrayList<>();
        for (var memberId : plan.memberIds()) {
            var participant = encounter.participant(memberId).orElse(null);
            if (participant == null || !participant.operational()) {
                continue;
            }
            if (participant.selectedTargetId()
                .filter(plan.targetId()::equals)
                .isPresent()) {
                continue;
            }

            commands.add(new SelectCombatTargetCommand(
                encounter.combatId(),
                memberId,
                plan.targetId(),
                Instant.EPOCH.plusMillis(plan.generatedTick())
            ));
        }
        return List.copyOf(commands);
    }
}
