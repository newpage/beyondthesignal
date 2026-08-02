package com.beyondsignal.game;

import io.vertx.core.Vertx;
import org.flywaydb.core.Flyway;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        Config config = Config.fromEnvironment();
        prepareDatabase(config);

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new PlatformVerticle(config))
            .onSuccess(id -> System.out.println(
                "Beyond the Signal game server deployed: " + id
            ))
            .onFailure(error -> {
                error.printStackTrace();
                vertx.close();
                System.exit(1);
            });
    }

    private static void prepareDatabase(Config config) {
        if (!config.databaseEnabled()) {
            System.out.println(
                "Developer mode: database migration is disabled. "
                    + "Set APP_MODE=production or DATABASE_ENABLED=true "
                    + "to enable PostgreSQL and Flyway."
            );
            return;
        }

        System.out.println(
            "Database enabled. Running Flyway migrations against "
                + config.jdbcUrl()
        );
        migrate(config);
    }

    private static void migrate(Config config) {
        Flyway.configure()
            .dataSource(
                config.jdbcUrl(),
                config.postgresUser(),
                config.postgresPassword()
            )
            .locations("classpath:db/migration")
            .load()
            .migrate();
    }
}
