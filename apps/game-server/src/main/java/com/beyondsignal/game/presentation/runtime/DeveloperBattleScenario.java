package com.beyondsignal.game.presentation.runtime;

public enum DeveloperBattleScenario {
    FLEET_SKIRMISH("Fleet Skirmish", "Three Alliance ships defend against a hostile strike force."),
    COMMAND_AMBUSH("Command Ambush", "An Alliance command group survives a reinforced hostile attack.");

    private final String displayName;
    private final String description;

    DeveloperBattleScenario(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String displayName() {
        return displayName;
    }

    public String description() {
        return description;
    }
}
