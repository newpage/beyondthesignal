package com.beyondsignal.game.world;

import com.beyondsignal.game.galaxy.Coordinates;
import com.beyondsignal.game.galaxy.Galaxy;
import com.beyondsignal.game.galaxy.Planet;
import com.beyondsignal.game.galaxy.PlanetType;
import com.beyondsignal.game.galaxy.Sector;
import com.beyondsignal.game.galaxy.StarSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class UniverseGenerator {
    private static final String[] STAR_CLASSES = {"O", "B", "A", "F", "G", "K", "M"};
    private static final String[] SYSTEM_PREFIXES = {
        "Astra", "Vega", "Deneb", "Sirius", "Altair", "Rigel", "Kepler", "Orion",
        "Epsilon", "Tau", "Nova", "Helios", "Arcturus", "Proxima", "Cygnus", "Draconis"
    };

    public Galaxy generate(long seed, int sectorCount, int systemsPerSector) {
        if (sectorCount < 1 || systemsPerSector < 1) {
            throw new IllegalArgumentException("sectorCount and systemsPerSector must be at least 1");
        }

        Random random = new Random(seed);
        List<Sector> sectors = new ArrayList<>();

        for (int sectorIndex = 0; sectorIndex < sectorCount; sectorIndex++) {
            List<StarSystem> systems = new ArrayList<>();
            for (int systemIndex = 0; systemIndex < systemsPerSector; systemIndex++) {
                systems.add(generateSystem(random, sectorIndex, systemIndex));
            }

            sectors.add(new Sector(
                "sector-" + sectorIndex,
                phoneticSectorName(sectorIndex),
                systems
            ));
        }

        return new Galaxy(seed, sectors);
    }

    private StarSystem generateSystem(Random random, int sectorIndex, int systemIndex) {
        String id = "system-" + sectorIndex + "-" + systemIndex;
        String name = SYSTEM_PREFIXES[random.nextInt(SYSTEM_PREFIXES.length)]
            + "-" + (sectorIndex + 1) + (char) ('A' + systemIndex % 26);

        Coordinates coordinates = new Coordinates(
            sectorIndex * 40.0 + random.nextDouble() * 30.0,
            random.nextDouble() * 100.0,
            random.nextDouble() * 30.0
        );

        int planetCount = 1 + random.nextInt(8);
        List<Planet> planets = new ArrayList<>();
        for (int orbit = 1; orbit <= planetCount; orbit++) {
            planets.add(generatePlanet(random, id, name, orbit));
        }

        return new StarSystem(
            id,
            name,
            coordinates,
            STAR_CLASSES[random.nextInt(STAR_CLASSES.length)],
            planets,
            random.nextDouble() < 0.12,
            random.nextDouble() < 0.18,
            false,
            false
        );
    }

    private Planet generatePlanet(Random random, String systemId, String systemName, int orbit) {
        PlanetType type = PlanetType.values()[random.nextInt(PlanetType.values().length)];
        boolean habitable = (type == PlanetType.TERRESTRIAL || type == PlanetType.OCEANIC)
            && random.nextDouble() < 0.35;

        return new Planet(
            systemId + "-planet-" + orbit,
            systemName + " " + roman(orbit),
            type,
            orbit,
            habitable,
            random.nextDouble() < 0.45,
            random.nextDouble() < 0.15
        );
    }

    private static String phoneticSectorName(int index) {
        String[] names = {"Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Eta", "Theta"};
        return names[index % names.length] + " Sector " + (index / names.length + 1);
    }

    private static String roman(int number) {
        String[] values = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII"};
        return number >= 1 && number <= values.length ? values[number - 1] : Integer.toString(number);
    }
}
