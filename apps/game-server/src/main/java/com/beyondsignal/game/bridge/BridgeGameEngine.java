package com.beyondsignal.game.bridge;

import com.beyondsignal.game.event.ExplorationChoice;
import com.beyondsignal.game.event.ExplorationEventController;
import com.beyondsignal.game.exploration.ExplorationCampaign;
import com.beyondsignal.game.galaxy.Galaxy;
import com.beyondsignal.game.galaxy.StarSystem;
import com.beyondsignal.game.navigation.WarpRoute;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public final class BridgeGameEngine {
    private final ExplorationCampaign campaign;
    private final Galaxy galaxy;
    private final BridgeCrewAssignments crew = new BridgeCrewAssignments();
    private final PowerGrid power = new PowerGrid();
    private final BridgeEventHub eventHub = new BridgeEventHub();
    private final ExplorationEventController explorationEvents = new ExplorationEventController();
    private final List<BridgeMessage> messages = new ArrayList<>();

    private long version;
    private long messageSequence;
    private AlertStatus alertStatus = AlertStatus.NORMAL;
    private double headingDegrees;
    private int throttle;
    private boolean shieldsRaised;
    private int shieldStrength = 100;
    private int hullIntegrity = 100;
    private String selectedTargetId;
    private int torpedoesRemaining = 6;
    private WarpRoute plottedRoute;

    public BridgeGameEngine(ExplorationCampaign campaign, Galaxy galaxy) {
        this.campaign = Objects.requireNonNull(campaign, "campaign");
        this.galaxy = Objects.requireNonNull(galaxy, "galaxy");
    }

    public synchronized void assign(BridgeStation station, UUID playerId) {
        crew.assign(station, playerId);
        changed(station, "INFO", playerId + " assigned to " + station);
    }

    public synchronized BridgeCommandResult submit(BridgeCommand command) {
        Objects.requireNonNull(command, "command");
        if (!crew.authorized(command.station(), command.playerId())) {
            return BridgeCommandResult.rejected(
                command.commandId(),
                "Player is not assigned to " + command.station(),
                version
            );
        }

        try {
            authorize(command);
            String message = execute(command);
            changed(command.station(), "INFO", message);
            return BridgeCommandResult.accepted(command.commandId(), message, version);
        } catch (RuntimeException exception) {
            return BridgeCommandResult.rejected(command.commandId(), exception.getMessage(), version);
        }
    }

    public synchronized void advance(Duration delta) {
        campaign.advance(delta);
        changed(BridgeStation.CAPTAIN, "STATUS", "Simulation advanced by " + delta);
    }

    public synchronized BridgeState snapshot() {
        return new BridgeState(
            version,
            alertStatus,
            headingDegrees,
            throttle,
            shieldsRaised,
            shieldStrength,
            hullIntegrity,
            selectedTargetId,
            torpedoesRemaining,
            power.snapshot(),
            crew.snapshot(),
            campaign.snapshot(),
            List.copyOf(messages)
        );
    }

    public BridgeEventHub eventHub() {
        return eventHub;
    }

    private void authorize(BridgeCommand command) {
        BridgeStation required = switch (command.type()) {
            case SET_ALERT_STATUS -> BridgeStation.CAPTAIN;
            case SET_HEADING, SET_THROTTLE, PLOT_COURSE, ENGAGE_WARP -> BridgeStation.HELM;
            case RUN_LONG_RANGE_SCAN -> BridgeStation.SCIENCE;
            case RAISE_SHIELDS, LOWER_SHIELDS, SELECT_TARGET, FIRE_PHASER, FIRE_TORPEDO ->
                BridgeStation.TACTICAL;
            case ALLOCATE_POWER -> BridgeStation.ENGINEERING;
            case OPEN_CHANNEL, RESPOND_TO_EVENT -> BridgeStation.COMMUNICATIONS;
        };
        if (command.station() != required) {
            throw new IllegalArgumentException(command.type() + " requires " + required);
        }
    }

    private String execute(BridgeCommand command) {
        return switch (command.type()) {
            case SET_ALERT_STATUS -> setAlert(command);
            case SET_HEADING -> setHeading(command);
            case SET_THROTTLE -> setThrottle(command);
            case PLOT_COURSE -> plotCourse(command);
            case ENGAGE_WARP -> engageWarp();
            case RUN_LONG_RANGE_SCAN -> scan(command);
            case RAISE_SHIELDS -> setShields(true);
            case LOWER_SHIELDS -> setShields(false);
            case SELECT_TARGET -> selectTarget(command);
            case FIRE_PHASER -> firePhaser();
            case FIRE_TORPEDO -> fireTorpedo();
            case ALLOCATE_POWER -> allocatePower(command);
            case OPEN_CHANNEL -> openChannel(command);
            case RESPOND_TO_EVENT -> respondToEvent(command);
        };
    }

    private String setAlert(BridgeCommand command) {
        alertStatus = AlertStatus.valueOf(command.required("status").toUpperCase(Locale.ROOT));
        return "Alert status set to " + alertStatus;
    }

    private String setHeading(BridgeCommand command) {
        double value = Double.parseDouble(command.required("headingDegrees"));
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Heading must be finite");
        }
        headingDegrees = ((value % 360.0) + 360.0) % 360.0;
        return "Heading set to " + headingDegrees;
    }

    private String setThrottle(BridgeCommand command) {
        int value = Integer.parseInt(command.required("throttle"));
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Throttle must be between 0 and 100");
        }
        throttle = value;
        return "Throttle set to " + throttle + "%";
    }

    private String plotCourse(BridgeCommand command) {
        plottedRoute = campaign.plotCourse(
            command.required("destinationSystemId"),
            Integer.parseInt(command.required("warpFactor"))
        );
        return "Course plotted to " + plottedRoute.destination().name();
    }

    private String engageWarp() {
        if (plottedRoute == null) {
            throw new IllegalStateException("No course is plotted");
        }
        if (power.allocation(Subsystem.ENGINES) < 20) {
            throw new IllegalStateException("Insufficient engine power for warp");
        }
        campaign.engage(plottedRoute);
        WarpRoute engaged = plottedRoute;
        plottedRoute = null;
        return "Warp engaged for " + engaged.destination().name();
    }

    private String scan(BridgeCommand command) {
        if (power.allocation(Subsystem.SENSORS) < 10) {
            throw new IllegalStateException("Insufficient sensor power");
        }
        double range = Double.parseDouble(command.required("rangeLightYears"));
        long seed = Long.parseLong(command.required("scanSeed"));
        int contacts = campaign.scan(range, power.allocation(Subsystem.SENSORS) / 100.0, seed)
            .contacts().size();
        return "Long-range scan complete: " + contacts + " contacts";
    }

    private String setShields(boolean raised) {
        if (raised && power.allocation(Subsystem.SHIELDS) < 10) {
            throw new IllegalStateException("Insufficient shield power");
        }
        shieldsRaised = raised;
        return raised ? "Shields raised" : "Shields lowered";
    }

    private String selectTarget(BridgeCommand command) {
        selectedTargetId = command.required("targetId");
        return "Target selected: " + selectedTargetId;
    }

    private String firePhaser() {
        requireTarget();
        if (power.allocation(Subsystem.WEAPONS) < 15) {
            throw new IllegalStateException("Insufficient weapons power");
        }
        return "Phasers fired at " + selectedTargetId;
    }

    private String fireTorpedo() {
        requireTarget();
        if (torpedoesRemaining == 0) {
            throw new IllegalStateException("No torpedoes remain");
        }
        torpedoesRemaining--;
        return "Torpedo launched at " + selectedTargetId;
    }

    private String allocatePower(BridgeCommand command) {
        Subsystem subsystem = Subsystem.valueOf(
            command.required("subsystem").toUpperCase(Locale.ROOT)
        );
        int amount = Integer.parseInt(command.required("amount"));
        power.allocate(subsystem, amount);
        return subsystem + " power set to " + amount + "%";
    }

    private String openChannel(BridgeCommand command) {
        return "Channel opened to " + command.required("recipient");
    }

    private String respondToEvent(BridgeCommand command) {
        String eventId = command.required("eventId");
        ExplorationChoice choice = ExplorationChoice.valueOf(
            command.required("choice").toUpperCase(Locale.ROOT)
        );
        return explorationEvents.resolve(
            eventId,
            choice,
            Long.parseLong(command.required("resolutionSeed"))
        ).outcome().narrative();
    }

    private void requireTarget() {
        if (selectedTargetId == null) {
            throw new IllegalStateException("No tactical target selected");
        }
    }

    private void changed(BridgeStation station, String severity, String text) {
        version++;
        messages.add(new BridgeMessage(
            ++messageSequence,
            station,
            severity,
            text,
            Instant.now()
        ));
        if (messages.size() > 100) {
            messages.removeFirst();
        }
        eventHub.publish(snapshot());
    }

    public synchronized String createExplorationEvent(
        String systemId,
        long tick,
        long seed
    ) {
        StarSystem system = galaxy.systemById(systemId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown system: " + systemId));
        String id = explorationEvents.generate(system, tick, seed).id();
        changed(BridgeStation.COMMUNICATIONS, "EVENT", "New exploration event: " + id);
        return id;
    }
}
