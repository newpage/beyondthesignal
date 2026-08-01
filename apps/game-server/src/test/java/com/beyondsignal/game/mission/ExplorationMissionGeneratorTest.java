package com.beyondsignal.game.mission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.galaxy.Coordinates;
import com.beyondsignal.game.galaxy.StarSystem;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExplorationMissionGeneratorTest {
    @Test
    void generationIsDeterministic() {
        StarSystem target = new StarSystem(
            "vega", "Vega", new Coordinates(1, 2, 3), "A",
            List.of(), true, false, false, false
        );
        ExplorationMissionGenerator generator = new ExplorationMissionGenerator();

        ExplorationMission first = generator.generate(target, 42L);
        ExplorationMission second = generator.generate(target, 42L);

        assertEquals(first, second);
        assertEquals(ExplorationMissionStatus.AVAILABLE, first.status());
        assertTrue(first.rewardCredits() >= 250);
    }
}
