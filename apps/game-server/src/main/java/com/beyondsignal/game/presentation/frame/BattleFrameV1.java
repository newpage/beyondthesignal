package com.beyondsignal.game.presentation.frame;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record BattleFrameV1(
    BattleFrameMetadata metadata,
    BattleCapabilities capabilities,
    List<BattleShipView> ships,
    List<BattleFormationView> formations,
    List<BattleMovementView> movement,
    List<BattleProjectileView> projectiles,
    List<BattleWreckView> wrecks,
    List<BattleFleetView> fleets,
    List<BattleSquadronView> squadrons,
    List<BattleFleetOrderView> orders,
    List<BattleEventView> events,
    Map<String, String> debug
) {
    public static final String VERSION = "1.0";

    public BattleFrameV1 {
        metadata = Objects.requireNonNull(metadata, "metadata");
        capabilities = Objects.requireNonNull(capabilities, "capabilities");
        ships = List.copyOf(ships);
        formations = List.copyOf(formations);
        movement = List.copyOf(movement);
        projectiles = List.copyOf(projectiles);
        wrecks = List.copyOf(wrecks);
        fleets = List.copyOf(fleets);
        squadrons = List.copyOf(squadrons);
        orders = List.copyOf(orders);
        events = List.copyOf(events);
        debug = Map.copyOf(debug);
        if (!VERSION.equals(metadata.frameVersion())) {
            throw new IllegalArgumentException("Unsupported frame version");
        }
    }

    public BattleFrameV1(
        BattleFrameMetadata metadata,
        BattleCapabilities capabilities,
        List<BattleShipView> ships,
        List<BattleFormationView> formations,
        List<BattleMovementView> movement,
        List<BattleEventView> events,
        Map<String, String> debug
    ) {
        this(
            metadata,
            capabilities,
            ships,
            formations,
            movement,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            events,
            debug
        );
    }

public BattleFrameV1(
    BattleFrameMetadata metadata,
    BattleCapabilities capabilities,
    List<BattleShipView> ships,
    List<BattleFormationView> formations,
    List<BattleMovementView> movement,
    List<BattleProjectileView> projectiles,
    List<BattleEventView> events,
    Map<String, String> debug
) {
    this(
        metadata,
        capabilities,
        ships,
        formations,
        movement,
        projectiles,
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        events,
        debug
    );
}

    public BattleFrameV1 withMetadata(BattleFrameMetadata value) {
        return new BattleFrameV1(
            value,
            capabilities,
            ships,
            formations,
            movement,
            projectiles,
            wrecks,
            fleets,
            squadrons,
            orders,
            events,
            debug
        );
    }
}
