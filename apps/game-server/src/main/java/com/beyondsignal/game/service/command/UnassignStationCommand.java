package com.beyondsignal.game.service.command;
import com.beyondsignal.game.domain.BridgeStation;
import java.util.Objects;
import java.util.UUID;
public record UnassignStationCommand(UUID sessionId, UUID requestingPlayerId, BridgeStation station) {
    public UnassignStationCommand {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        Objects.requireNonNull(requestingPlayerId, "requestingPlayerId must not be null");
        Objects.requireNonNull(station, "station must not be null");
    }
}
