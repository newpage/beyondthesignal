package com.beyondsignal.game.combat.scenario;

import com.beyondsignal.game.combat.command.FireCombatWeaponCommand;
import com.beyondsignal.game.combat.command.SelectCombatTargetCommand;
import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.engine.CombatEncounterStatus;
import com.beyondsignal.game.combat.engine.CombatParticipant;
import com.beyondsignal.game.combat.engine.CombatSimulationEngine;
import com.beyondsignal.game.combat.engine.CombatTickResult;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.replay.CombatRecorder;
import com.beyondsignal.game.combat.shield.ShieldModel;
import com.beyondsignal.game.combat.stats.CombatStatistics;
import com.beyondsignal.game.combat.weapon.WeaponMountState;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class CombatScenarioRunner {
    public CombatScenarioResult run(CombatScenario scenario) {
        CombatSimulationEngine engine = new CombatSimulationEngine();
        CombatEncounter encounter = engine.createEncounter(scenario.combatId(), scenario.seed());
        CombatRecorder recorder = new CombatRecorder();

        for (CombatScenarioParticipant definition : scenario.participants()) {
            List<WeaponMountState> mounts = new ArrayList<>();
            for (int index = 0; index < definition.weapons().size(); index++) {
                mounts.add(WeaponMountState.ready(
                    deterministicMountId(definition.participantId(), index),
                    definition.weapons().get(index),
                    100
                ));
            }
            encounter.addParticipant(new CombatParticipant(
                definition.participantId(),
                definition.side(),
                ShieldModel.uniform(definition.shieldStrength(), 1),
                mounts,
                definition.hull(),
                definition.targetingQuality(),
                definition.evasion()
            ));
        }
        encounter.start();

        long ticks = 0;
        while (ticks < scenario.maximumTicks()
            && encounter.status() == CombatEncounterStatus.ACTIVE) {
            queueAutomaticCommands(encounter, scenario.engagementDistance());
            CombatTickResult tick = engine.tick(scenario.combatId());
            recorder.record(tick);
            ticks++;
        }

        List<CombatSide> survivingSides = encounter.participants().stream()
            .filter(CombatParticipant::operational)
            .map(CombatParticipant::side)
            .filter(side -> side != CombatSide.NEUTRAL)
            .distinct()
            .toList();

        CombatSide winner = survivingSides.size() == 1
            ? survivingSides.getFirst()
            : null;

        return new CombatScenarioResult(
            ticks,
            encounter.status(),
            winner,
            CombatStatistics.from(encounter.events()),
            recorder.finish(encounter)
        );
    }

    private static void queueAutomaticCommands(
        CombatEncounter encounter,
        double distance
    ) {
        for (CombatParticipant actor : encounter.participants()) {
            if (!actor.operational()) {
                continue;
            }

            CombatParticipant target = actor.selectedTargetId()
                .flatMap(encounter::participant)
                .filter(CombatParticipant::operational)
                .orElseGet(() -> encounter.participants().stream()
                    .filter(candidate -> candidate.operational())
                    .filter(candidate -> candidate.side() != actor.side())
                    .findFirst()
                    .orElse(null));

            if (target == null) {
                continue;
            }

            if (actor.selectedTargetId().isEmpty()) {
                encounter.submit(new SelectCombatTargetCommand(
                    encounter.combatId(),
                    actor.participantId(),
                    target.participantId(),
                    Instant.EPOCH
                ));
            }

            actor.weapons().stream()
                .filter(WeaponMountState::readyToFire)
                .findFirst()
                .ifPresent(weapon -> encounter.submit(new FireCombatWeaponCommand(
                    encounter.combatId(),
                    actor.participantId(),
                    weapon.mountId(),
                    distance,
                    1.0,
                    Instant.EPOCH
                )));
        }
    }

    private static UUID deterministicMountId(UUID participantId, int index) {
        return UUID.nameUUIDFromBytes(
            (participantId + ":weapon:" + index)
                .getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }
}
