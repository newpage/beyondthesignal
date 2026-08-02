package com.beyondsignal.game.presentation.pipeline;

import com.beyondsignal.game.presentation.buffer.BattleFrameBuffer;
import com.beyondsignal.game.presentation.context.PresentationContext;
import com.beyondsignal.game.presentation.events.BattleFrameReadyEvent;
import com.beyondsignal.game.presentation.events.PresentationEventBus;
import com.beyondsignal.game.presentation.frame.BattleFrameV1;
import com.beyondsignal.game.presentation.mapper.BattlePresentationMapper;
import com.beyondsignal.game.presentation.publisher.BattleFramePublisher;
import com.beyondsignal.game.presentation.service.BattlePresentationService;
import java.util.Objects;

public final class PresentationPipeline {
    private final BattlePresentationMapper mapper;
    private final BattlePresentationService presentationService;
    private final BattleFrameBuffer buffer;
    private final BattleFramePublisher publisher;
    private final PresentationEventBus eventBus;

    public PresentationPipeline(
        BattlePresentationMapper mapper,
        BattlePresentationService presentationService,
        BattleFrameBuffer buffer,
        BattleFramePublisher publisher,
        PresentationEventBus eventBus
    ) {
        this.mapper = Objects.requireNonNull(mapper, "mapper");
        this.presentationService = Objects.requireNonNull(
            presentationService,
            "presentationService"
        );
        this.buffer = Objects.requireNonNull(buffer, "buffer");
        this.publisher = Objects.requireNonNull(publisher, "publisher");
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
    }

    public BattleFrameV1 publish(PresentationContext context) {
        BattleFrameV1 frame = presentationService.publish(
            mapper.map(context)
        );
        buffer.append(frame);
        publisher.publish(frame);
        eventBus.publish(new BattleFrameReadyEvent(frame));
        return frame;
    }

    public BattleFrameBuffer buffer() {
        return buffer;
    }
}
