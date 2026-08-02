package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.beyondsignal.game.combat.ai.snapshot.CombatSnapshotFactory;
import com.beyondsignal.game.combat.engine.CombatParticipant;
import com.beyondsignal.game.combat.engine.CombatSimulationEngine;
import com.beyondsignal.game.combat.engine.CombatTickProcessor;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldModel;
import com.beyondsignal.game.presentation.buffer.BattleFrameBuffer;
import com.beyondsignal.game.presentation.checksum.BattleFrameChecksumService;
import com.beyondsignal.game.presentation.context.PresentationConfiguration;
import com.beyondsignal.game.presentation.context.PresentationDebugOptions;
import com.beyondsignal.game.presentation.events.PresentationEventBus;
import com.beyondsignal.game.presentation.integration.CombatPresentationCoordinator;
import com.beyondsignal.game.presentation.mapper.BattlePresentationMapper;
import com.beyondsignal.game.presentation.pipeline.PresentationPipeline;
import com.beyondsignal.game.presentation.publisher.NoOpBattleFramePublisher;
import com.beyondsignal.game.presentation.service.BattlePresentationService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatPresentationCoordinatorIntegrationTest {
    @Test
    void combatTickProducesSignedPresentationFrame() {
        BattleFrameBuffer buffer = new BattleFrameBuffer(10);
        CombatPresentationCoordinator coordinator =
            new CombatPresentationCoordinator(
                new CombatSnapshotFactory(),
                new PresentationPipeline(
                    new BattlePresentationMapper(),
                    new BattlePresentationService(
                        new BattleFrameChecksumService(),
                        List.of()
                    ),
                    buffer,
                    new NoOpBattleFramePublisher(),
                    new PresentationEventBus()
                ),
                PresentationConfiguration.defaults(),
                PresentationDebugOptions.development()
            );

        CombatSimulationEngine engine = new CombatSimulationEngine(
            new CombatTickProcessor(),
            coordinator
        );

        CombatId id = new CombatId(UUID.randomUUID());
        var encounter = engine.createEncounter(id, 123);
        encounter.addParticipant(participant(CombatSide.FRIENDLY));
        encounter.addParticipant(participant(CombatSide.HOSTILE));
        encounter.start();

        engine.tick(id);

        var frame = buffer.latest().orElseThrow();
        assertEquals(1L, frame.metadata().tick());
        assertEquals(id.value(), frame.metadata().battleId());
        assertEquals(2, frame.ships().size());
        assertFalse(frame.metadata().checksum().isBlank());
    }

    private static CombatParticipant participant(CombatSide side) {
        return new CombatParticipant(
            UUID.randomUUID(),
            side,
            ShieldModel.uniform(100, 0),
            List.of(),
            100,
            0.8,
            0.2
        );
    }
}
