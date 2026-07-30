package com.beyondsignal.game.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class GameSession {
    private final UUID id;
    private final String sessionName;
    private final String shipName;
    private final UUID hostPlayerId;
    private final Instant createdAt;
    private final Map<UUID, Player> players;
    private final Map<BridgeStation, StationAssignment> assignments;

    private SessionStatus status;
    private Instant startedAt;

    private GameSession(
        UUID id,
        String sessionName,
        String shipName,
        UUID hostPlayerId,
        Instant createdAt,
        SessionStatus status,
        Instant startedAt,
        Map<UUID, Player> players,
        Map<BridgeStation, StationAssignment> assignments
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.sessionName = requireText(sessionName, "sessionName");
        this.shipName = requireText(shipName, "shipName");
        this.hostPlayerId = Objects.requireNonNull(hostPlayerId, "hostPlayerId must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.startedAt = startedAt;
        this.players = new LinkedHashMap<>(Objects.requireNonNull(players, "players must not be null"));
        this.assignments = new EnumMap<>(BridgeStation.class);
        this.assignments.putAll(Objects.requireNonNull(assignments, "assignments must not be null"));

        Player host = this.players.get(hostPlayerId);
        if (host == null || !host.host()) {
            throw new IllegalArgumentException("hostPlayerId must identify the host player");
        }
    }

    public static GameSession create(String sessionName, String shipName, String hostName, Instant createdAt) {
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Player host = Player.host(hostName, createdAt);
        Map<UUID, Player> players = new LinkedHashMap<>();
        players.put(host.id(), host);

        return new GameSession(
            UUID.randomUUID(),
            sessionName,
            shipName,
            host.id(),
            createdAt,
            SessionStatus.CREATED,
            null,
            players,
            Map.of()
        );
    }

    public void transitionTo(SessionStatus target, Instant occurredAt) {
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");

        if (!status.canTransitionTo(target)) {
            throw new DomainException("Invalid session transition: " + status + " -> " + target);
        }

        status = target;
        if (target == SessionStatus.RUNNING && startedAt == null) {
            startedAt = occurredAt;
        }
    }

    public void addPlayer(Player player) {
        Objects.requireNonNull(player, "player must not be null");
        if (status == SessionStatus.STARTING || status == SessionStatus.RUNNING || status == SessionStatus.ENDED) {
            throw new DomainException("Players cannot join a session in status " + status);
        }
        if (players.containsKey(player.id())) {
            throw new DomainException("Player is already part of the session: " + player.id());
        }
        boolean duplicateName = players.values().stream()
            .anyMatch(existing -> existing.displayName().equalsIgnoreCase(player.displayName()));
        if (duplicateName) {
            throw new DomainException("Display name is already in use: " + player.displayName());
        }
        players.put(player.id(), player);
    }

    public void removePlayer(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId must not be null");
        if (hostPlayerId.equals(playerId)) {
            throw new DomainException("The host player cannot leave the session");
        }
        if (players.remove(playerId) == null) {
            throw new DomainException("Player is not part of the session: " + playerId);
        }
        assignments.entrySet().removeIf(entry -> entry.getValue().playerId().equals(playerId));
    }

    public void assignStation(BridgeStation station, UUID playerId, Instant assignedAt) {
        Objects.requireNonNull(station, "station must not be null");
        Objects.requireNonNull(playerId, "playerId must not be null");
        Objects.requireNonNull(assignedAt, "assignedAt must not be null");

        if (!players.containsKey(playerId)) {
            throw new DomainException("Player is not part of the session: " + playerId);
        }
        if (station != BridgeStation.OBSERVER) {
            assignments.values().stream()
                .filter(existing -> existing.station() != BridgeStation.OBSERVER)
                .filter(existing -> existing.playerId().equals(playerId))
                .findFirst()
                .ifPresent(existing -> {
                    throw new DomainException("Player is already assigned to " + existing.station());
                });
        }
        if (assignments.containsKey(station)) {
            throw new DomainException("Station is already assigned: " + station);
        }
        assignments.put(station, new StationAssignment(station, playerId, assignedAt));
    }

    public void unassignStation(BridgeStation station) {
        Objects.requireNonNull(station, "station must not be null");
        assignments.remove(station);
    }

    public UUID id() { return id; }
    public String sessionName() { return sessionName; }
    public String shipName() { return shipName; }
    public UUID hostPlayerId() { return hostPlayerId; }
    public Instant createdAt() { return createdAt; }
    public SessionStatus status() { return status; }
    public Optional<Instant> startedAt() { return Optional.ofNullable(startedAt); }
    public List<Player> players() { return Collections.unmodifiableList(new ArrayList<>(players.values())); }
    public Map<BridgeStation, StationAssignment> assignments() { return Collections.unmodifiableMap(assignments); }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
