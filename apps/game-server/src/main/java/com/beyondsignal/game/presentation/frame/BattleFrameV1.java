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
        events = List.copyOf(events);
        debug = Map.copyOf(debug);
        if (!VERSION.equals(metadata.frameVersion())) {
            throw new IllegalArgumentException("Unsupported frame version");
        }
    }

    public BattleFrameV1 withMetadata(BattleFrameMetadata value) {
        return new BattleFrameV1(
            value, capabilities, ships, formations, movement, events, debug
        );
    }
}
