package com.beyondsignal.game.mission;

import com.beyondsignal.game.exploration.Faction;
import com.beyondsignal.game.galaxy.StarSystem;
import java.util.Random;

public final class ExplorationMissionGenerator {
    public ExplorationMission generate(StarSystem target, long seed) {
        Random random = new Random(seed ^ target.id().hashCode());
        ExplorationMissionType type = ExplorationMissionType.values()[
            random.nextInt(ExplorationMissionType.values().length)
        ];
        Faction sponsor = Faction.values()[random.nextInt(Faction.values().length - 1)];

        return new ExplorationMission(
            target.id() + "-" + type.name().toLowerCase(),
            type,
            title(type, target.name()),
            target.id(),
            sponsor,
            250 + random.nextInt(751),
            2 + random.nextInt(9),
            ExplorationMissionStatus.AVAILABLE
        );
    }

    private static String title(ExplorationMissionType type, String systemName) {
        return switch (type) {
            case SURVEY_SYSTEM -> "Survey " + systemName;
            case INVESTIGATE_ANOMALY -> "Investigate anomaly near " + systemName;
            case DELIVER_SUPPLIES -> "Deliver supplies to " + systemName;
            case RESCUE_OPERATION -> "Rescue operation in " + systemName;
            case DIPLOMATIC_ENVOY -> "Diplomatic envoy to " + systemName;
        };
    }
}
