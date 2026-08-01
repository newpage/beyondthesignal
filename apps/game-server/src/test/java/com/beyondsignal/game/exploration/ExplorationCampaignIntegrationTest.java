package com.beyondsignal.game.exploration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.galaxy.Galaxy;
import com.beyondsignal.game.galaxy.StarSystem;
import com.beyondsignal.game.navigation.WarpRoute;
import com.beyondsignal.game.world.UniverseGenerator;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExplorationCampaignIntegrationTest {
    @Test
    void supportsTravelScanMissionAndSnapshotLoop() {
        Galaxy galaxy = new UniverseGenerator().generate(2026L, 1, 5);
        List<StarSystem> systems = galaxy.systems().toList();
        ExplorationCampaign campaign = new ExplorationCampaign(
            galaxy, systems.get(0).id(), Instant.EPOCH
        );

        campaign.scan(500.0, 1.0, 7L);
        assertTrue(campaign.explorationMap().contacts().size() > 0);

        WarpRoute route = campaign.plotCourse(systems.get(1).id(), 6);
        campaign.engage(route);
        campaign.advance(route.estimatedTravelTime());

        assertEquals(systems.get(1).id(), campaign.travel().shipPosition().currentSystemId());

        campaign.generateMission(systems.get(2).id(), 11L);
        assertEquals(1, campaign.snapshot().missions().size());
        assertEquals(1L, campaign.snapshot().simulationTick());
        assertTrue(campaign.shipResources().fuel() < campaign.shipResources().maximumFuel());
    }
}
