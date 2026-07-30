package com.beyondsignal.game.service.command;
import com.beyondsignal.game.domain.BridgeStation;
import java.util.Objects;
import java.util.UUID;
public record AssignStationCommand(UUID sessionId, UUID requestingPlayerId, UUID playerId, BridgeStation station) {
    public AssignStationCommand {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        Objects.requireNonNull(requestingPlayerId, "requestingPlayerId must not be null");
        Objects.requireNonNull(playerId, "playerId must not be null");
        Objects.requireNonNull(station, "station must not be null");
    }
}
