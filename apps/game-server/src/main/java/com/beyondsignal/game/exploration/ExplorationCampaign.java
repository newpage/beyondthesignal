package com.beyondsignal.game.exploration;

import com.beyondsignal.game.galaxy.Galaxy;
import com.beyondsignal.game.galaxy.StarSystem;
import com.beyondsignal.game.mission.ExplorationMission;
import com.beyondsignal.game.mission.ExplorationMissionGenerator;
import com.beyondsignal.game.navigation.NavigationComputer;
import com.beyondsignal.game.navigation.ShipPosition;
import com.beyondsignal.game.navigation.WarpDrive;
import com.beyondsignal.game.navigation.WarpRoute;
import com.beyondsignal.game.sensor.LongRangeSensorArray;
import com.beyondsignal.game.sensor.SensorController;
import com.beyondsignal.game.sensor.SensorScanResult;
import com.beyondsignal.game.simulation.TickEngine;
import com.beyondsignal.game.simulation.WarpTravelController;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ExplorationCampaign {
    private final Galaxy galaxy;
    private final NavigationComputer navigation;
    private final TickEngine tickEngine;
    private final WarpTravelController travel;
    private final SensorController sensors;
    private final ExplorationMap explorationMap = new ExplorationMap();
    private final ReputationLedger reputation = new ReputationLedger();
    private final CargoHold cargo = new CargoHold(100);
    private final List<ExplorationMission> missions = new ArrayList<>();
    private ShipResources shipResources = ShipResources.initial();
    private long tick;

    public ExplorationCampaign(Galaxy galaxy, String startingSystemId, Instant initialTime) {
        this.galaxy = Objects.requireNonNull(galaxy, "galaxy");
        StarSystem start = galaxy.systemById(startingSystemId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown starting system"));

        navigation = new NavigationComputer(galaxy);
        tickEngine = new TickEngine(initialTime);
        travel = new WarpTravelController(
            new WarpDrive(),
            ShipPosition.atSystem(start.id(), start.coordinates())
        );
        sensors = new SensorController(new LongRangeSensorArray(galaxy));
        tickEngine.addListener(travel);
        tickEngine.addListener(sensors);
    }

    public WarpRoute plotCourse(String destinationSystemId, int warpFactor) {
        String origin = travel.shipPosition().currentSystemId();
        return navigation.calculateRoute(origin, destinationSystemId, warpFactor);
    }

    public void engage(WarpRoute route) {
        int fuelCost = Math.max(1, (int) Math.ceil(route.distanceLightYears()));
        shipResources = shipResources.consumeFuel(fuelCost);
        travel.engage(route, tickEngine.simulationTime());
    }

    public void advance(Duration delta) {
        tickEngine.advance(delta);
        tick++;
    }

    public SensorScanResult scan(double range, double power, long seed) {
        String systemId = travel.shipPosition().currentSystemId();
        SensorScanResult result = sensors.scan(
            systemId, range, power, seed, tickEngine.simulationTime()
        );
        explorationMap.apply(result);
        return result;
    }

    public ExplorationMission generateMission(String targetSystemId, long seed) {
        StarSystem target = galaxy.systemById(targetSystemId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown target system"));
        ExplorationMission mission = new ExplorationMissionGenerator().generate(target, seed);
        missions.add(mission);
        return mission;
    }

    public ExplorationCampaignState snapshot() {
        return new ExplorationCampaignState(
            galaxy.seed(),
            travel.shipPosition(),
            shipResources,
            cargo.snapshot(),
            reputation.snapshot(),
            List.copyOf(explorationMap.contacts().keySet()),
            List.copyOf(missions),
            tick
        );
    }

    public ShipResources shipResources() {
        return shipResources;
    }

    public CargoHold cargo() {
        return cargo;
    }

    public ReputationLedger reputation() {
        return reputation;
    }

    public ExplorationMap explorationMap() {
        return explorationMap;
    }

    public WarpTravelController travel() {
        return travel;
    }

    public SensorController sensors() {
        return sensors;
    }
}
