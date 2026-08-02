package com.beyondsignal.game.combat.fleet.squadron;

import com.beyondsignal.game.combat.command.CombatCommand;
import java.util.List;

public record SquadronCoordinationResult(
    List<SquadronExecutionPlan> plans,
    List<CombatCommand> commands
) {
    public SquadronCoordinationResult {
        plans = List.copyOf(plans);
        commands = List.copyOf(commands);
    }
}
