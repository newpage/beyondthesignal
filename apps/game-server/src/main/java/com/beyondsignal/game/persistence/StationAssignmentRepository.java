package com.beyondsignal.game.persistence;

import com.beyondsignal.game.domain.BridgeStation;
import com.beyondsignal.game.domain.StationAssignment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StationAssignmentRepository {
    StationAssignment save(UUID sessionId, StationAssignment assignment);

    Optional<StationAssignment> find(UUID sessionId, BridgeStation station);

    List<StationAssignment> findBySessionId(UUID sessionId);

    void delete(UUID sessionId, BridgeStation station);
}
