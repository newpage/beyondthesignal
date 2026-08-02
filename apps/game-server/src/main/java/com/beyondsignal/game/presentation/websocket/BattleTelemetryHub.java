package com.beyondsignal.game.presentation.websocket;

import com.beyondsignal.game.presentation.frame.BattleFrameV1;
import com.beyondsignal.game.presentation.publisher.BattleFramePublisher;
import com.beyondsignal.game.presentation.serialization.BattleFrameSerializer;
import io.vertx.core.http.ServerWebSocket;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public final class BattleTelemetryHub implements BattleFramePublisher {
    private final Set<ServerWebSocket> clients = ConcurrentHashMap.newKeySet();
    private final AtomicReference<BattleFrameV1> latest = new AtomicReference<>();
    private final BattleFrameSerializer serializer;

    public BattleTelemetryHub() {
        this(new BattleFrameSerializer());
    }

    BattleTelemetryHub(BattleFrameSerializer serializer) {
        this.serializer = serializer;
    }

    public void connect(ServerWebSocket socket) {
        clients.add(socket);
        socket.closeHandler(ignored -> clients.remove(socket));
        socket.exceptionHandler(ignored -> clients.remove(socket));
        BattleFrameV1 frame = latest.get();
        if (frame != null) {
            socket.writeTextMessage(serializer.toJson(frame));
        }
    }

    @Override
    public void publish(BattleFrameV1 frame) {
        latest.set(frame);
        String payload = serializer.toJson(frame);
        clients.forEach(socket -> socket.writeTextMessage(payload));
    }

    public Optional<BattleFrameV1> latest() {
        return Optional.ofNullable(latest.get());
    }

    public int clientCount() {
        return clients.size();
    }
}
