package com.beyondsignal.game.bridge;

import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.exploration.ExplorationCampaign;
import com.beyondsignal.game.galaxy.Galaxy;
import com.beyondsignal.game.galaxy.StarSystem;
import com.beyondsignal.game.world.UniverseGenerator;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class BridgeSnapshotJsonTest {
    @Test
    void serializesCoreBridgeFields() {
        Galaxy galaxy = new UniverseGenerator().generate(1L, 1, 2);
        StarSystem start = galaxy.systems().findFirst().orElseThrow();
        BridgeGameEngine engine = new BridgeGameEngine(
            new ExplorationCampaign(galaxy, start.id(), Instant.EPOCH),
            galaxy
        );

        String json = new BridgeSnapshotJson().serialize(engine.snapshot());

        assertTrue(json.contains("\"alertStatus\":\"NORMAL\""));
        assertTrue(json.contains("\"powerAllocations\""));
        assertTrue(json.contains("\"campaignTick\":0"));
    }
}
