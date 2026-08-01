package com.beyondsignal.game.combat.ai.planner;

import com.beyondsignal.game.combat.ai.goal.CombatAiGoal;
import com.beyondsignal.game.combat.command.CombatCommand;
import java.util.List;
import java.util.Objects;

public record CombatAiPlan(
    CombatAiGoal goal,
    List<CombatCommand> commands
) {
    public CombatAiPlan {
        goal = Objects.requireNonNull(goal, "goal");
        commands = List.copyOf(Objects.requireNonNull(commands, "commands"));
    }
}
