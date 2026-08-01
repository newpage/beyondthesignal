package com.beyondsignal.game.persistence;

import com.beyondsignal.game.exploration.ExplorationCampaignState;
import com.beyondsignal.game.exploration.Faction;
import com.beyondsignal.game.exploration.ResourceType;
import com.beyondsignal.game.exploration.ShipResources;
import com.beyondsignal.game.mission.ExplorationMission;
import com.beyondsignal.game.mission.ExplorationMissionStatus;
import com.beyondsignal.game.mission.ExplorationMissionType;
import com.beyondsignal.game.navigation.ShipPosition;
import com.beyondsignal.game.galaxy.Coordinates;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class ExplorationStateCodec {
    public String encode(ExplorationCampaignState state) {
        Properties properties = new Properties();
        properties.setProperty("galaxySeed", Long.toString(state.galaxySeed()));
        properties.setProperty("simulationTick", Long.toString(state.simulationTick()));
        properties.setProperty("position.x", Double.toString(state.shipPosition().coordinates().x()));
        properties.setProperty("position.y", Double.toString(state.shipPosition().coordinates().y()));
        properties.setProperty("position.z", Double.toString(state.shipPosition().coordinates().z()));
        properties.setProperty("position.currentSystemId", nullable(state.shipPosition().currentSystemId()));
        properties.setProperty("position.destinationSystemId", nullable(state.shipPosition().destinationSystemId()));
        properties.setProperty("position.progress", Double.toString(state.shipPosition().routeProgress()));
        properties.setProperty("ship.fuel", Integer.toString(state.shipResources().fuel()));
        properties.setProperty("ship.maximumFuel", Integer.toString(state.shipResources().maximumFuel()));
        properties.setProperty("ship.hull", Integer.toString(state.shipResources().hullIntegrity()));
        properties.setProperty("ship.maximumHull", Integer.toString(state.shipResources().maximumHull()));
        properties.setProperty("ship.credits", Integer.toString(state.shipResources().credits()));

        state.cargo().forEach((type, quantity) ->
            properties.setProperty("cargo." + type.name(), Integer.toString(quantity))
        );
        state.reputation().forEach((faction, value) ->
            properties.setProperty("reputation." + faction.name(), Integer.toString(value))
        );

        properties.setProperty("discovered", String.join(",", state.discoveredContactIds()));
        properties.setProperty("missions.count", Integer.toString(state.missions().size()));
        for (int i = 0; i < state.missions().size(); i++) {
            ExplorationMission mission = state.missions().get(i);
            String prefix = "mission." + i + ".";
            properties.setProperty(prefix + "id", mission.id());
            properties.setProperty(prefix + "type", mission.type().name());
            properties.setProperty(prefix + "title", mission.title());
            properties.setProperty(prefix + "target", mission.targetSystemId());
            properties.setProperty(prefix + "sponsor", mission.sponsor().name());
            properties.setProperty(prefix + "credits", Integer.toString(mission.rewardCredits()));
            properties.setProperty(prefix + "reputation", Integer.toString(mission.rewardReputation()));
            properties.setProperty(prefix + "status", mission.status().name());
        }

        StringBuilder builder = new StringBuilder();
        properties.stringPropertyNames().stream().sorted().forEach(name ->
            builder.append(escape(name)).append('=')
                .append(escape(properties.getProperty(name))).append('\n')
        );
        return builder.toString();
    }

    public ExplorationCampaignState decode(String encoded) {
        Properties properties = new Properties();
        for (String line : encoded.lines().toList()) {
            if (line.isBlank()) {
                continue;
            }
            int separator = line.indexOf('=');
            if (separator < 1) {
                throw new IllegalArgumentException("Invalid state line: " + line);
            }
            properties.setProperty(
                unescape(line.substring(0, separator)),
                unescape(line.substring(separator + 1))
            );
        }

        ShipPosition position = new ShipPosition(
            new Coordinates(
                doubleValue(properties, "position.x"),
                doubleValue(properties, "position.y"),
                doubleValue(properties, "position.z")
            ),
            emptyToNull(properties.getProperty("position.currentSystemId")),
            emptyToNull(properties.getProperty("position.destinationSystemId")),
            doubleValue(properties, "position.progress")
        );

        ShipResources resources = new ShipResources(
            intValue(properties, "ship.fuel"),
            intValue(properties, "ship.maximumFuel"),
            intValue(properties, "ship.hull"),
            intValue(properties, "ship.maximumHull"),
            intValue(properties, "ship.credits")
        );

        EnumMap<ResourceType, Integer> cargo = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            String key = "cargo." + type.name();
            if (properties.containsKey(key)) {
                cargo.put(type, intValue(properties, key));
            }
        }

        EnumMap<Faction, Integer> reputation = new EnumMap<>(Faction.class);
        for (Faction faction : Faction.values()) {
            String key = "reputation." + faction.name();
            if (properties.containsKey(key)) {
                reputation.put(faction, intValue(properties, key));
            }
        }

        String discovered = properties.getProperty("discovered", "");
        List<String> discoveredIds = discovered.isBlank()
            ? List.of()
            : List.of(discovered.split(","));

        int missionCount = intValue(properties, "missions.count");
        List<ExplorationMission> missions = new ArrayList<>();
        for (int i = 0; i < missionCount; i++) {
            String prefix = "mission." + i + ".";
            missions.add(new ExplorationMission(
                required(properties, prefix + "id"),
                ExplorationMissionType.valueOf(required(properties, prefix + "type")),
                required(properties, prefix + "title"),
                required(properties, prefix + "target"),
                Faction.valueOf(required(properties, prefix + "sponsor")),
                intValue(properties, prefix + "credits"),
                intValue(properties, prefix + "reputation"),
                ExplorationMissionStatus.valueOf(required(properties, prefix + "status"))
            ));
        }

        return new ExplorationCampaignState(
            longValue(properties, "galaxySeed"),
            position,
            resources,
            cargo,
            reputation,
            discoveredIds,
            missions,
            longValue(properties, "simulationTick")
        );
    }

    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing property: " + key);
        }
        return value;
    }

    private static int intValue(Properties properties, String key) {
        return Integer.parseInt(required(properties, key));
    }

    private static long longValue(Properties properties, String key) {
        return Long.parseLong(required(properties, key));
    }

    private static double doubleValue(Properties properties, String key) {
        return Double.parseDouble(required(properties, key));
    }

    private static String nullable(String value) {
        return value == null ? "" : value;
    }

    private static String emptyToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\n", "\\n").replace("=", "\\=");
    }

    private static String unescape(String value) {
        StringBuilder builder = new StringBuilder();
        boolean escaped = false;
        for (char ch : value.toCharArray()) {
            if (escaped) {
                builder.append(ch == 'n' ? '\n' : ch);
                escaped = false;
            } else if (ch == '\\') {
                escaped = true;
            } else {
                builder.append(ch);
            }
        }
        if (escaped) {
            builder.append('\\');
        }
        return builder.toString();
    }
}
