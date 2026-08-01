package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.command.CombatCommand;
import com.beyondsignal.game.combat.event.CombatEvent;
import com.beyondsignal.game.combat.log.CombatLog;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;

/**
 * Authoritative state container for one deterministic combat encounter.
 */
public final class CombatEncounter {
    private final CombatContext context;
    private final Map<UUID, CombatParticipant> participants = new LinkedHashMap<>();
    private final Queue<CombatCommand> commandQueue = new ArrayDeque<>();
    private final CombatLog combatLog = new CombatLog();

    private CombatEncounterStatus status = CombatEncounterStatus.INITIALIZING;

    public CombatEncounter(CombatContext context) {
        this.context = Objects.requireNonNull(context, "context");
    }

    public CombatId combatId() {
        return context.combatId();
    }

    public CombatContext context() {
        return context;
    }

    public synchronized CombatEncounterStatus status() {
        return status;
    }

    public synchronized void addParticipant(CombatParticipant participant) {
        Objects.requireNonNull(participant, "participant");
        if (status != CombatEncounterStatus.INITIALIZING) {
            throw new IllegalStateException("Participants can only be added during initialization");
        }
        if (participants.putIfAbsent(participant.participantId(), participant) != null) {
            throw new IllegalArgumentException("Duplicate participant: " + participant.participantId());
        }
    }

    public synchronized void start() {
        if (status != CombatEncounterStatus.INITIALIZING) {
            throw new IllegalStateException("Encounter is already started");
        }
        if (participants.size() < 2) {
            throw new IllegalStateException("Combat requires at least two participants");
        }
        status = CombatEncounterStatus.ACTIVE;
    }

    public synchronized Optional<CombatParticipant> participant(UUID id) {
        return Optional.ofNullable(participants.get(id));
    }

    public synchronized Collection<CombatParticipant> participants() {
        return List.copyOf(participants.values());
    }

    public synchronized void submit(CombatCommand command) {
        Objects.requireNonNull(command, "command");
        if (status != CombatEncounterStatus.ACTIVE) {
            throw new IllegalStateException("Encounter is not active");
        }
        if (!command.combatId().equals(combatId())) {
            throw new IllegalArgumentException("Command belongs to another combat encounter");
        }
        if (!participants.containsKey(command.actorId())) {
            throw new IllegalArgumentException("Unknown combat actor: " + command.actorId());
        }
        commandQueue.add(command);
    }

    public synchronized List<CombatCommand> drainCommands() {
        List<CombatCommand> drained = List.copyOf(commandQueue);
        commandQueue.clear();
        return drained;
    }

    public synchronized int queuedCommandCount() {
        return commandQueue.size();
    }

    public synchronized void appendEvent(CombatEvent event) {
        combatLog.append(event);
    }

    public synchronized List<CombatEvent> events() {
        return combatLog.events();
    }

    public synchronized long nextEventSequence() {
        return combatLog.size() + 1L;
    }

    public synchronized void evaluateCompletion() {
        long activeSides = participants.values().stream()
            .filter(CombatParticipant::operational)
            .map(CombatParticipant::side)
            .filter(side -> side != CombatSide.NEUTRAL)
            .distinct()
            .count();

        if (status == CombatEncounterStatus.ACTIVE && activeSides <= 1) {
            status = CombatEncounterStatus.COMPLETED;
        }
    }
}
