package com.beyondsignal.game.service.command;
public record CreateSessionCommand(String sessionName, String shipName, String hostDisplayName) {
    public CreateSessionCommand {
        sessionName = requireText(sessionName, "sessionName");
        shipName = requireText(shipName, "shipName");
        hostDisplayName = requireText(hostDisplayName, "hostDisplayName");
    }
    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(fieldName + " must not be blank");
        return value.trim();
    }
}
