package com.beyondsignal.game.presentation.runtime;

import com.beyondsignal.game.combat.command.FireCombatWeaponCommand;
import com.beyondsignal.game.combat.command.SelectCombatTargetCommand;
import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.engine.CombatParticipant;
import com.beyondsignal.game.combat.engine.CombatSimulationEngine;
import com.beyondsignal.game.combat.fleet.FleetDoctrine;
import com.beyondsignal.game.combat.fleet.FleetOrder;
import com.beyondsignal.game.combat.fleet.FleetOrderType;
import com.beyondsignal.game.combat.fleet.FleetState;
import com.beyondsignal.game.combat.fleet.SquadronState;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldModel;
import com.beyondsignal.game.combat.weapon.DamageType;
import com.beyondsignal.game.combat.weapon.WeaponArc;
import com.beyondsignal.game.combat.weapon.WeaponDefinition;
import com.beyondsignal.game.combat.weapon.WeaponMountState;
import com.beyondsignal.game.combat.weapon.WeaponType;
import io.vertx.core.Vertx;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class DeveloperBattleRuntime implements AutoCloseable {
    private static final double ENGAGEMENT_DISTANCE = 1_000.0;
    private static final WeaponDefinition DEMO_PROJECTILE = new WeaponDefinition(
        "developer-torpedo",
        "Developer Torpedo",
        WeaponType.PROJECTILE,
        DamageType.KINETIC,
        WeaponArc.FORWARD,
        32,
        10_000.0,
        0.88,
        0,
        24,
        1
    );

    private static final WeaponDefinition DEMO_BEAM = new WeaponDefinition(
        "developer-phaser",
        "Developer Phaser",
        WeaponType.BEAM,
        DamageType.ENERGY,
        WeaponArc.FORWARD,
        18,
        10_000.0,
        0.95,
        0,
        8,
        0
    );

    private final Vertx vertx;
    private final CombatSimulationEngine engine;
    private final CombatId combatId;
    private long timerId = -1;

    public DeveloperBattleRuntime(
        Vertx vertx,
        CombatSimulationEngine engine,
        long seed
    ) {
        this.vertx = vertx;
        this.engine = engine;
        this.combatId = new CombatId(UUID.nameUUIDFromBytes(
            ("developer-battle-" + seed).getBytes()
        ));

        var encounter = engine.createEncounter(combatId, seed);
        CombatParticipant allianceCommand = participant(
            "alliance-command",
            CombatSide.FRIENDLY
        );
        CombatParticipant allianceEscortOne = participant(
            "alliance-escort-1",
            CombatSide.FRIENDLY
        );
        CombatParticipant allianceEscortTwo = participant(
            "alliance-escort-2",
            CombatSide.FRIENDLY
        );
        CombatParticipant hostileCommand = participant(
            "hostile-command",
            CombatSide.HOSTILE
        );
        CombatParticipant hostileRaider = participant(
            "hostile-raider",
            CombatSide.HOSTILE
        );

        encounter.addParticipant(allianceCommand);
        encounter.addParticipant(allianceEscortOne);
        encounter.addParticipant(allianceEscortTwo);
        encounter.addParticipant(hostileCommand);
        encounter.addParticipant(hostileRaider);

        registerDeveloperFleets(
            encounter,
            allianceCommand,
            allianceEscortOne,
            allianceEscortTwo,
            hostileCommand,
            hostileRaider
        );

        encounter.start();

        selectTarget(allianceCommand, hostileCommand);
        selectTarget(allianceEscortOne, hostileRaider);
        selectTarget(allianceEscortTwo, hostileCommand);
        selectTarget(hostileCommand, allianceCommand);
        selectTarget(hostileRaider, allianceEscortOne);
    }

    private static void registerDeveloperFleets(
        CombatEncounter encounter,
        CombatParticipant allianceCommand,
        CombatParticipant allianceEscortOne,
        CombatParticipant allianceEscortTwo,
        CombatParticipant hostileCommand,
        CombatParticipant hostileRaider
    ) {
        FleetOrder allianceOrder = new FleetOrder(
            stableId("alliance-fleet-order"),
            FleetOrderType.DEFEND,
            1,
            allianceCommand.participantId(),
            Map.of("objective", "hold-command-line")
        );
        SquadronState allianceSquadron = new SquadronState(
            stableId("alliance-alpha-squadron"),
            "Alpha Squadron",
            allianceCommand.participantId(),
            List.of(
                allianceCommand.participantId(),
                allianceEscortOne.participantId(),
                allianceEscortTwo.participantId()
            ),
            "WEDGE",
            allianceOrder,
            hostileCommand.participantId(),
            1.0,
            "READY"
        );
        encounter.addFleet(new FleetState(
            stableId("alliance-task-force"),
            "Alliance Task Force",
            "Admiral Hayes",
            CombatSide.FRIENDLY,
            FleetDoctrine.DEFENSIVE,
            List.of(allianceSquadron),
            List.of(allianceOrder)
        ));

        FleetOrder hostileOrder = new FleetOrder(
            stableId("hostile-fleet-order"),
            FleetOrderType.ATTACK,
            1,
            allianceCommand.participantId(),
            Map.of("objective", "destroy-command-ship")
        );
        SquadronState hostileSquadron = new SquadronState(
            stableId("hostile-assault-squadron"),
            "Assault Squadron",
            hostileCommand.participantId(),
            List.of(
                hostileCommand.participantId(),
                hostileRaider.participantId()
            ),
            "LINE",
            hostileOrder,
            allianceCommand.participantId(),
            0.9,
            "READY"
        );
        encounter.addFleet(new FleetState(
            stableId("hostile-strike-force"),
            "Hostile Strike Force",
            "War Leader Kor",
            CombatSide.HOSTILE,
            FleetDoctrine.AGGRESSIVE,
            List.of(hostileSquadron),
            List.of(hostileOrder)
        ));
    }

    private static UUID stableId(String value) {
        return UUID.nameUUIDFromBytes(value.getBytes());
    }

    public void start(long periodMillis) {
        if (periodMillis <= 0) {
            throw new IllegalArgumentException("periodMillis must be positive");
        }
        if (timerId != -1) {
            return;
        }
        timerId = vertx.setPeriodic(periodMillis, ignored -> tick());
        tick();
    }

    private void tick() {
        CombatEncounter encounter = engine.encounter(combatId).orElse(null);
        if (encounter == null) {
            close();
            return;
        }

        submitReadyWeapons(encounter);
        engine.tick(combatId);

        if (engine.completed(combatId)) {
            close();
        }
    }

    private void submitReadyWeapons(CombatEncounter encounter) {
        for (CombatParticipant participant : encounter.participants()) {
            if (!participant.operational()
                || participant.selectedTargetId().isEmpty()) {
                continue;
            }

            participant.weapons().stream()
                .filter(WeaponMountState::readyToFire)
                .sorted((left, right) -> {
                    boolean projectileTick =
                        encounter.context().clock().currentTick() % 30 == 0;
                    if (left.definition().type() == right.definition().type()) {
                        return left.mountId().compareTo(right.mountId());
                    }
                    if (projectileTick) {
                        return left.definition().type() == WeaponType.PROJECTILE
                            ? -1
                            : 1;
                    }
                    return left.definition().type() == WeaponType.BEAM
                        ? -1
                        : 1;
                })
                .findFirst()
                .ifPresent(weapon -> engine.submit(
                    new FireCombatWeaponCommand(
                        combatId,
                        participant.participantId(),
                        weapon.mountId(),
                        ENGAGEMENT_DISTANCE,
                        1.0,
                        Instant.EPOCH.plusMillis(
                            encounter.context().clock().currentTick()
                        )
                    )
                ));
        }
    }

    private void selectTarget(
        CombatParticipant actor,
        CombatParticipant target
    ) {
        engine.submit(new SelectCombatTargetCommand(
            combatId,
            actor.participantId(),
            target.participantId(),
            Instant.EPOCH
        ));
    }

    private static CombatParticipant participant(
        String stableName,
        CombatSide side
    ) {
        UUID participantId = UUID.nameUUIDFromBytes(stableName.getBytes());
        UUID mountId = UUID.nameUUIDFromBytes(
            (stableName + "-beam").getBytes()
        );
        return new CombatParticipant(
            participantId,
            side,
            ShieldModel.uniform(100, 1),
            List.of(
                WeaponMountState.ready(mountId, DEMO_BEAM, 0),
                WeaponMountState.ready(
                    UUID.nameUUIDFromBytes(
                        (stableName + "-torpedo").getBytes()
                    ),
                    DEMO_PROJECTILE,
                    8
                )
            ),
            100,
            0.8,
            0.2
        );
    }

    @Override
    public void close() {
        if (timerId != -1) {
            vertx.cancelTimer(timerId);
            timerId = -1;
        }
    }
}
