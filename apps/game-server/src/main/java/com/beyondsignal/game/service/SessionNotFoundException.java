package com.beyondsignal.game.service;
import java.util.UUID;
public final class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(UUID sessionId) { super("Game session was not found: " + sessionId); }
}
