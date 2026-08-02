package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.presentation.events.BattleFrameReadyListener;
import com.beyondsignal.game.presentation.events.PresentationEventBus;
import org.junit.jupiter.api.Test;

class PresentationEventBusTest {
    @Test
    void supportsSubscriptionAndRemoval() {
        PresentationEventBus bus = new PresentationEventBus();
        BattleFrameReadyListener listener = event -> {};

        bus.subscribe(listener);
        assertEquals(1, bus.listenerCount());
        assertTrue(bus.unsubscribe(listener));
        assertEquals(0, bus.listenerCount());
    }
}
