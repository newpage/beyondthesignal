package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.fleet.FleetDoctrine;
import com.beyondsignal.game.combat.fleet.FleetOrder;
import com.beyondsignal.game.combat.fleet.FleetOrderType;
import com.beyondsignal.game.combat.fleet.FleetState;
import com.beyondsignal.game.combat.fleet.SquadronState;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.presentation.context.PresentationConfiguration;
import com.beyondsignal.game.presentation.context.PresentationContext;
import com.beyondsignal.game.presentation.context.PresentationDebugOptions;
import com.beyondsignal.game.presentation.mapper.BattlePresentationMapper;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FleetPresentationMapperTest {
    @Test
    void mapsFleetsSquadronsAndOrdersDeterministically() {
        UUID leader = UUID.randomUUID();
        FleetOrder lowPriority = new FleetOrder(
            UUID.randomUUID(),
            FleetOrderType.HOLD_POSITION,
            5,
            null,
            Map.of()
        );
        FleetOrder highPriority = new FleetOrder(
            UUID.randomUUID(),
            FleetOrderType.ATTACK,
            1,
            UUID.randomUUID(),
            Map.of("mode", "focus")
        );
        SquadronState squadron = new SquadronState(
            UUID.randomUUID(),
            "Alpha",
            leader,
            List.of(leader),
            "WEDGE",
            highPriority,
            highPriority.targetId(),
            0.9,
            "READY"
        );
        FleetState fleet = new FleetState(
            UUID.randomUUID(),
            "Seventh Fleet",
            "Admiral",
            CombatSide.FRIENDLY,
            FleetDoctrine.DEFENSIVE,
            List.of(squadron),
            List.of(lowPriority, highPriority)
        );

        var frame = new BattlePresentationMapper().map(
            new PresentationContext(
                new CombatAiSnapshot(CombatId.random(), 10, List.of()),
                7,
                1,
                Instant.EPOCH,
                PresentationConfiguration.defaults(),
                PresentationDebugOptions.disabled(),
                List.of(),
                List.of(),
                List.of(),
                List.of(fleet)
            )
        );

        assertEquals(1, frame.fleets().size());
        assertEquals(1, frame.squadrons().size());
        assertEquals(2, frame.orders().size());
        assertEquals(highPriority.orderId(), frame.orders().getFirst().id());
        assertEquals(lowPriority.orderId(), frame.orders().getLast().id());
    }
}
