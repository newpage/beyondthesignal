package com.beyondsignal.game.simulation;

import java.util.Objects;
import java.util.UUID;

public record MissionState(
    UUID id,
    String title,
    String objective,
    MissionStatus status,
    int score,
    long startedAtTick,
    Long completedAtTick
) {
    public MissionState {
        Objects.requireNonNull(id, "id must not be null");
        title = requireText(title, "title");
        objective = requireText(objective, "objective");
        Objects.requireNonNull(status, "status must not be null");
        if (score < 0) throw new IllegalArgumentException("score must not be negative");
        if (startedAtTick < 0) throw new IllegalArgumentException("startedAtTick must not be negative");
        if (completedAtTick != null && completedAtTick < startedAtTick) {
            throw new IllegalArgumentException("completedAtTick must not precede startedAtTick");
        }
    }

    public MissionState complete(long tick, int awardedScore) {
        if (status != MissionStatus.ACTIVE) return this;
        return new MissionState(id, title, objective, MissionStatus.COMPLETED,
            score + awardedScore, startedAtTick, tick);
    }

    public MissionState fail(long tick) {
        if (status != MissionStatus.ACTIVE) return this;
        return new MissionState(id, title, objective, MissionStatus.FAILED,
            score, startedAtTick, tick);
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name + " must not be null");
        String result = value.trim();
        if (result.isEmpty()) throw new IllegalArgumentException(name + " must not be blank");
        return result;
    }
}
