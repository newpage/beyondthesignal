package com.beyondsignal.game.exploration;

import com.beyondsignal.game.mission.ExplorationMission;
import com.beyondsignal.game.navigation.ShipPosition;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record ExplorationCampaignState(
    long galaxySeed,
    ShipPosition shipPosition,
    ShipResources shipResources,
    Map<ResourceType, Integer> cargo,
    Map<Faction, Integer> reputation,
    List<String> discoveredContactIds,
    List<ExplorationMission> missions,
    long simulationTick
) {
    public ExplorationCampaignState {
        shipPosition = Objects.requireNonNull(shipPosition, "shipPosition");
        shipResources = Objects.requireNonNull(shipResources, "shipResources");
        cargo = Map.copyOf(Objects.requireNonNull(cargo, "cargo"));
        reputation = Map.copyOf(Objects.requireNonNull(reputation, "reputation"));
        discoveredContactIds = List.copyOf(
            Objects.requireNonNull(discoveredContactIds, "discoveredContactIds")
        );
        missions = List.copyOf(Objects.requireNonNull(missions, "missions"));
        if (simulationTick < 0) {
            throw new IllegalArgumentException("simulationTick cannot be negative");
        }
    }
}
