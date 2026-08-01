package com.beyondsignal.game.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.exploration.ExplorationCampaignState;
import com.beyondsignal.game.exploration.ShipResources;
import com.beyondsignal.game.galaxy.Coordinates;
import com.beyondsignal.game.navigation.ShipPosition;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FileExplorationStateRepositoryTest {
    @Test
    void savesAndLoadsState() throws Exception {
        Path directory = Files.createTempDirectory("bts-exploration");
        FileExplorationStateRepository repository =
            new FileExplorationStateRepository(directory.resolve("campaign.state"));

        ExplorationCampaignState state = new ExplorationCampaignState(
            1L,
            ShipPosition.atSystem("sol", new Coordinates(0, 0, 0)),
            ShipResources.initial(),
            Map.of(),
            Map.of(),
            List.of(),
            List.of(),
            9L
        );

        assertTrue(repository.load().isEmpty());
        repository.save(state);
        assertEquals(state, repository.load().orElseThrow());
    }
}
