package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.presentation.buffer.BattleFrameBuffer;
import com.beyondsignal.game.presentation.checksum.BattleFrameChecksumService;
import com.beyondsignal.game.presentation.context.PresentationConfiguration;
import com.beyondsignal.game.presentation.context.PresentationContext;
import com.beyondsignal.game.presentation.context.PresentationDebugOptions;
import com.beyondsignal.game.presentation.events.PresentationEventBus;
import com.beyondsignal.game.presentation.frame.BattleFrameV1;
import com.beyondsignal.game.presentation.mapper.BattlePresentationMapper;
import com.beyondsignal.game.presentation.pipeline.PresentationPipeline;
import com.beyondsignal.game.presentation.recorder.InMemoryFrameRecorder;
import com.beyondsignal.game.presentation.service.BattlePresentationService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PresentationPipelineTest {
    @Test
    void signsBuffersRecordsPublishesAndEmits() {
        InMemoryFrameRecorder recorder = new InMemoryFrameRecorder(10);
        BattleFrameBuffer buffer = new BattleFrameBuffer(10);
        List<BattleFrameV1> published = new ArrayList<>();
        List<BattleFrameV1> events = new ArrayList<>();
        PresentationEventBus bus = new PresentationEventBus();
        bus.subscribe(event -> events.add(event.frame()));

        PresentationPipeline pipeline = new PresentationPipeline(
            new BattlePresentationMapper(),
            new BattlePresentationService(
                new BattleFrameChecksumService(),
                List.of(recorder)
            ),
            buffer,
            published::add,
            bus
        );

        BattleFrameV1 result = pipeline.publish(new PresentationContext(
            new CombatAiSnapshot(
                new CombatId(UUID.randomUUID()),
                1,
                List.of()
            ),
            5,
            0,
            Instant.EPOCH,
            PresentationConfiguration.defaults(),
            PresentationDebugOptions.disabled()
        ));

        assertFalse(result.metadata().checksum().isBlank());
        assertEquals(result, buffer.latest().orElseThrow());
        assertEquals(List.of(result), recorder.frames());
        assertEquals(List.of(result), published);
        assertEquals(List.of(result), events);
    }
}
