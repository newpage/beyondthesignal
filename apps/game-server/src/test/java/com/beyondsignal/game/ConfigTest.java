package com.beyondsignal.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigTest {
    @Test
    void jdbcUrlUsesConfiguredDatabaseCoordinates() {
        Config config = new Config(8080, "db", 5433, "signal", "user", "secret", "cache", 6379, "http://ai");
        assertEquals("jdbc:postgresql://db:5433/signal", config.jdbcUrl());
    }
}
