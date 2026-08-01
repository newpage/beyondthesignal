package com.beyondsignal.game.bridge;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.exploration.ExplorationCampaign;
import com.beyondsignal.game.galaxy.Galaxy;
import com.beyondsignal.game.galaxy.StarSystem;
import com.beyondsignal.game.world.UniverseGenerator;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BridgeGameEngineTest {
    @Test
    void authorizesStationAndPublishesSharedState() {
        Galaxy galaxy = new UniverseGenerator().generate(2026L, 1, 5);
        List<StarSystem> systems = galaxy.systems().toList();
        ExplorationCampaign campaign = new ExplorationCampaign(
            galaxy, systems.getFirst().id(), Instant.EPOCH
        );
        BridgeGameEngine engine = new BridgeGameEngine(campaign, galaxy);
        UUID helm = UUID.randomUUID();
        engine.assign(BridgeStation.HELM, helm);

        var result = engine.submit(new BridgeCommand(
            UUID.randomUUID(),
            helm,
            BridgeStation.HELM,
            BridgeCommandType.SET_THROTTLE,
            Map.of("throttle", "65")
        ));

        assertTrue(result.accepted());
        assertTrue(engine.snapshot().version() >= 2);
        assertTrue(engine.snapshot().throttle() == 65);
    }

    @Test
    void rejectsPlayerWithoutStationAssignment() {
        Galaxy galaxy = new UniverseGenerator().generate(7L, 1, 3);
        StarSystem start = galaxy.systems().findFirst().orElseThrow();
        BridgeGameEngine engine = new BridgeGameEngine(
            new ExplorationCampaign(galaxy, start.id(), Instant.EPOCH),
            galaxy
        );

        var result = engine.submit(new BridgeCommand(
            UUID.randomUUID(),
            UUID.randomUUID(),
            BridgeStation.TACTICAL,
            BridgeCommandType.RAISE_SHIELDS,
            Map.of()
        ));

        assertFalse(result.accepted());
    }

    @Test
    void helmCanPlotAndEngageWarpUsingCampaignState() {
        Galaxy galaxy = new UniverseGenerator().generate(55L, 1, 4);
        List<StarSystem> systems = galaxy.systems().toList();
        BridgeGameEngine engine = new BridgeGameEngine(
            new ExplorationCampaign(galaxy, systems.getFirst().id(), Instant.EPOCH),
            galaxy
        );
        UUID helm = UUID.randomUUID();
        engine.assign(BridgeStation.HELM, helm);

        assertTrue(engine.submit(new BridgeCommand(
            UUID.randomUUID(),
            helm,
            BridgeStation.HELM,
            BridgeCommandType.PLOT_COURSE,
            Map.of(
                "destinationSystemId", systems.get(1).id(),
                "warpFactor", "6"
            )
        )).accepted());

        assertTrue(engine.submit(new BridgeCommand(
            UUID.randomUUID(),
            helm,
            BridgeStation.HELM,
            BridgeCommandType.ENGAGE_WARP,
            Map.of()
        )).accepted());

        assertTrue(engine.snapshot().campaign().shipPosition().inTransit());
    }
}
