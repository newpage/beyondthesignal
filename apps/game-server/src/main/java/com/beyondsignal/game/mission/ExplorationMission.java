package com.beyondsignal.game.mission;

import com.beyondsignal.game.exploration.Faction;

public record ExplorationMission(
    String id,
    ExplorationMissionType type,
    String title,
    String targetSystemId,
    Faction sponsor,
    int rewardCredits,
    int rewardReputation,
    ExplorationMissionStatus status
) {
    public ExplorationMission {
        if (id == null || id.isBlank() || title == null || title.isBlank()
            || targetSystemId == null || targetSystemId.isBlank()) {
            throw new IllegalArgumentException("Mission fields are required");
        }
        if (rewardCredits < 0 || rewardReputation < 0) {
            throw new IllegalArgumentException("Mission rewards cannot be negative");
        }
    }

    public ExplorationMission activate() {
        if (status != ExplorationMissionStatus.AVAILABLE) {
            throw new IllegalStateException("Mission is not available");
        }
        return withStatus(ExplorationMissionStatus.ACTIVE);
    }

    public ExplorationMission complete() {
        if (status != ExplorationMissionStatus.ACTIVE) {
            throw new IllegalStateException("Mission is not active");
        }
        return withStatus(ExplorationMissionStatus.COMPLETED);
    }

    public ExplorationMission fail() {
        if (status != ExplorationMissionStatus.ACTIVE) {
            throw new IllegalStateException("Mission is not active");
        }
        return withStatus(ExplorationMissionStatus.FAILED);
    }

    private ExplorationMission withStatus(ExplorationMissionStatus next) {
        return new ExplorationMission(
            id, type, title, targetSystemId, sponsor,
            rewardCredits, rewardReputation, next
        );
    }
}
