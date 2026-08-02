package com.beyondsignal.game.combat.fleet.squadron;

import static org.junit.jupiter.api.Assertions.assertNull;
import com.beyondsignal.game.combat.fleet.FleetDoctrine;
import com.beyondsignal.game.combat.fleet.ai.CommanderStatus;
import com.beyondsignal.game.combat.fleet.ai.FleetDecision;
import com.beyondsignal.game.combat.fleet.ai.FleetObjectiveType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SquadronTargetSelectorTest {
    @Test
    void returnsNullWhenNoOperationalThreatExists() {
        var encounter = FleetSquadronTestSupport.encounter();
        var decision = new FleetDecision(
            UUID.randomUUID(),
            FleetDoctrine.DEFENSIVE,
            FleetObjectiveType.HOLD_POSITION,
            null,
            List.of(),
            CommanderStatus.ACTIVE,
            false,
            1
        );

        assertNull(new SquadronTargetSelector().select(
            encounter,
            decision
        ));
    }
}
