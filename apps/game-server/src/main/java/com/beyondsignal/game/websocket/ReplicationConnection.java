package com.beyondsignal.game.websocket;

import com.beyondsignal.game.service.event.SessionEvent;
import com.beyondsignal.game.service.event.SessionEventType;
import com.beyondsignal.game.simulation.ShipState;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

final class ReplicationConnection implements Consumer<SessionEvent> {
    private final Consumer<String> outbound;
    private final ReplicationMessageSerializer serializer;
    private final AtomicLong highestShipTick = new AtomicLong(-1);

    ReplicationConnection(Consumer<String> outbound, ReplicationMessageSerializer serializer) {
        this.outbound = Objects.requireNonNull(outbound, "outbound must not be null");
        this.serializer = Objects.requireNonNull(serializer, "serializer must not be null");
    }

    @Override
    public void accept(SessionEvent event) {
        Objects.requireNonNull(event, "event must not be null");

        if (event.type() == SessionEventType.SHIP_STATE_UPDATED) {
            Object rawTick = event.payload().get("tick");
            if (!(rawTick instanceof Number number) || !advance(number.longValue())) {
                return;
            }
        }

        send(serializer.serialize(event));
    }

    void sendSnapshot(ShipState state) {
        Objects.requireNonNull(state, "state must not be null");
        if (advance(state.tick())) {
            send(serializer.serializeSnapshot(state));
        }
    }

    long highestShipTick() {
        return highestShipTick.get();
    }

    private boolean advance(long candidate) {
        while (true) {
            long current = highestShipTick.get();
            if (candidate <= current) {
                return false;
            }
            if (highestShipTick.compareAndSet(current, candidate)) {
                return true;
            }
        }
    }

    private void send(JsonObject message) {
        outbound.accept(message.encode());
    }
}
