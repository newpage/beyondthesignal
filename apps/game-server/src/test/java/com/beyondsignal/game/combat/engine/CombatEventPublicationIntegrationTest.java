package com.beyondsignal.game.combat.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.beyondsignal.game.combat.command.SelectCombatTargetCommand;
import com.beyondsignal.game.combat.event.CombatEventBatch;
import com.beyondsignal.game.combat.event.CombatEventBus;
import com.beyondsignal.game.combat.event.CombatEventRecorder;
import com.beyondsignal.game.combat.event.CombatEventType;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldModel;
import com.beyondsignal.game.presentation.integration.CombatTickPresentationHook;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatEventPublicationIntegrationTest {
    @Test
    void publishesTickEventsAfterDeterministicProcessing() {
        CombatEventBus bus = new CombatEventBus();
        CombatEventRecorder recorder = new CombatEventRecorder();
        List<CombatEventBatch> observed = new ArrayList<>();
        bus.subscribe(recorder);
        bus.subscribe(observed::add);

        CombatSimulationEngine engine = new CombatSimulationEngine(
            new CombatTickProcessor(),
            CombatTickPresentationHook.noOp(),
            bus
        );
        CombatId combatId = CombatId.random();
        CombatEncounter encounter = engine.createEncounter(combatId, 91L);
        CombatParticipant attacker = participant(CombatSide.FRIENDLY);
        CombatParticipant target = participant(CombatSide.HOSTILE);
        encounter.addParticipant(attacker);
        encounter.addParticipant(target);
        encounter.start();

        engine.submit(new SelectCombatTargetCommand(
            combatId,
            attacker.participantId(),
            target.participantId(),
            Instant.EPOCH
        ));
        engine.tick(combatId);

        assertEquals(1, observed.size());
        assertEquals(1L, observed.getFirst().tick());
        assertEquals(CombatEventType.TARGET_SELECTED,
            observed.getFirst().events().getFirst().type());
        assertEquals(observed.getFirst().events(), recorder.events(combatId));
    }

    private static CombatParticipant participant(CombatSide side) {
        return new CombatParticipant(
            UUID.randomUUID(),
            side,
            ShieldModel.uniform(50, 1),
            List.of(),
            100,
            0.8,
            0.2
        );
    }
}
