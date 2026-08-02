package com.beyondsignal.game;

public record Config(
    int port,
    String postgresHost,
    int postgresPort,
    String postgresDatabase,
    String postgresUser,
    String postgresPassword,
    String redisHost,
    int redisPort,
    String aiServiceUrl
) {
    private static final String DEVELOPMENT_MODE = "development";
    private static final String PRODUCTION_MODE = "production";

    public static Config fromEnvironment() {
        return new Config(
            integer("GAME_SERVER_PORT", 8080),
            value("POSTGRES_HOST", "localhost"),
            integer("POSTGRES_PORT", 5432),
            value("POSTGRES_DB", "beyond_signal"),
            value("POSTGRES_USER", "beyond_signal"),
            value("POSTGRES_PASSWORD", "change-me"),
            value("REDIS_HOST", "localhost"),
            integer("REDIS_PORT", 6379),
            value("AI_SERVICE_URL", "http://localhost:8000")
        );
    }

    public String jdbcUrl() {
        return "jdbc:postgresql://%s:%d/%s".formatted(
            postgresHost,
            postgresPort,
            postgresDatabase
        );
    }

    public String applicationMode() {
        return value("APP_MODE", DEVELOPMENT_MODE).trim().toLowerCase();
    }

    public boolean isProductionMode() {
        return PRODUCTION_MODE.equals(applicationMode());
    }

    public boolean databaseEnabled() {
        String configured = System.getenv("DATABASE_ENABLED");
        if (configured != null && !configured.isBlank()) {
            return Boolean.parseBoolean(configured);
        }
        return isProductionMode();
    }

    private static String value(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static int integer(String name, int fallback) {
        try {
            return Integer.parseInt(value(name, Integer.toString(fallback)));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
