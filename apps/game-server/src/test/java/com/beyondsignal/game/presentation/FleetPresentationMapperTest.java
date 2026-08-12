package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.fleet.FleetDoctrine;
import com.beyondsignal.game.combat.fleet.FleetOrder;
import com.beyondsignal.game.combat.fleet.FleetOrderType;
import com.beyondsignal.game.combat.fleet.FleetState;
import com.beyondsignal.game.combat.fleet.SquadronState;
import com.beyondsignal.game.combat.fleet.ai.CommanderStatus;
import com.beyondsignal.game.combat.fleet.ai.FleetDecision;
import com.beyondsignal.game.combat.fleet.ai.FleetObjectiveType;
import com.beyondsignal.game.combat.fleet.ai.ThreatScore;
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

    @Test
    void mapsFleetDecisionTelemetry() {
        UUID fleetId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        FleetDecision decision = new FleetDecision(
            fleetId,
            FleetDoctrine.AGGRESSIVE,
            FleetObjectiveType.ENGAGE_PRIMARY_THREAT,
            targetId,
            List.of(new ThreatScore(targetId, 42.5, 8, 0.75)),
            CommanderStatus.ACTIVE,
            false,
            19
        );

        var frame = new BattlePresentationMapper().map(
            new PresentationContext(
                new CombatAiSnapshot(CombatId.random(), 19, List.of()),
                7,
                2,
                Instant.EPOCH,
                PresentationConfiguration.defaults(),
                PresentationDebugOptions.disabled(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(decision)
            )
        );

        assertEquals(1, frame.fleetDecisions().size());
        var view = frame.fleetDecisions().getFirst();
        assertEquals(fleetId, view.fleetId());
        assertEquals("AGGRESSIVE", view.doctrine());
        assertEquals("ENGAGE_PRIMARY_THREAT", view.objective());
        assertEquals(targetId, view.primaryTargetId());
        assertEquals(42.5, view.threats().getFirst().score());
        assertEquals("ACTIVE", view.commanderStatus());
        assertEquals(19, view.generatedTick());
    }
}
