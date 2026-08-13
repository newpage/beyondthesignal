package com.beyondsignal.game.presentation.runtime;

import java.util.UUID;

public record DeveloperBattleStatus(
    UUID battleId,
    DeveloperBattleScenario scenario,
    String state,
    long tick,
    String outcome,
    int allianceOperational,
    int hostileOperational
) {}
