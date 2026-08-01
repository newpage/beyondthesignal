package com.beyondsignal.game.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.exploration.ExplorationCampaignState;
import com.beyondsignal.game.exploration.Faction;
import com.beyondsignal.game.exploration.ResourceType;
import com.beyondsignal.game.exploration.ShipResources;
import com.beyondsignal.game.galaxy.Coordinates;
import com.beyondsignal.game.mission.ExplorationMission;
import com.beyondsignal.game.mission.ExplorationMissionStatus;
import com.beyondsignal.game.mission.ExplorationMissionType;
import com.beyondsignal.game.navigation.ShipPosition;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ExplorationStateCodecTest {
    @Test
    void roundTripsCampaignState() {
        ExplorationCampaignState state = new ExplorationCampaignState(
            77L,
            ShipPosition.atSystem("sol", new Coordinates(1, 2, 3)),
            ShipResources.initial(),
            Map.of(ResourceType.SUPPLIES, 4),
            Map.of(Faction.ALLIANCE, 8),
            List.of("vega"),
            List.of(new ExplorationMission(
                "m1",
                ExplorationMissionType.SURVEY_SYSTEM,
                "Survey Vega",
                "vega",
                Faction.ALLIANCE,
                500,
                5,
                ExplorationMissionStatus.ACTIVE
            )),
            123L
        );

        ExplorationStateCodec codec = new ExplorationStateCodec();
        assertEquals(state, codec.decode(codec.encode(state)));
    }
}
