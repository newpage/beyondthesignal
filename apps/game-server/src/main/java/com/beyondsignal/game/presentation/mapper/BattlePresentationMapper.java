package com.beyondsignal.game.presentation.mapper;

import com.beyondsignal.game.combat.ai.snapshot.CombatantSnapshot;
import com.beyondsignal.game.combat.event.CombatEvent;
import com.beyondsignal.game.presentation.context.PresentationContext;
import com.beyondsignal.game.presentation.frame.BattleEventView;
import com.beyondsignal.game.presentation.frame.BattleFrameMetadata;
import com.beyondsignal.game.presentation.frame.BattleFrameV1;
import com.beyondsignal.game.presentation.frame.BattleShipView;
import com.beyondsignal.game.presentation.frame.BattleProjectileView;
import com.beyondsignal.game.presentation.frame.PresentationVector;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class BattlePresentationMapper {
    private static final double MOTION_STEP = 0.12;

    public BattleFrameV1 map(PresentationContext context) {
        return new BattleFrameV1(
            new BattleFrameMetadata(
                context.snapshot().combatId().value(),
                context.snapshot().tick(),
                context.simulationTimeSeconds(),
                context.seed(),
                BattleFrameV1.VERSION,
                context.generatedAt(),
                ""
            ),
            context.configuration().capabilities(),
            mapShips(context),
            java.util.List.of(),
            java.util.List.of(),
            mapProjectiles(context),
            mapEvents(context),
            debug(context)
        );
    }

    private java.util.List<BattleShipView> mapShips(
        PresentationContext context
    ) {
        var sorted = context.snapshot().combatants().stream()
            .sorted((left, right) ->
                left.participantId().compareTo(right.participantId()))
            .toList();
        java.util.List<BattleShipView> ships = new java.util.ArrayList<>();
        for (int index = 0; index < sorted.size(); index++) {
            ships.add(ship(sorted.get(index), index, context.snapshot().tick()));
        }
        return java.util.List.copyOf(ships);
    }

    private BattleShipView ship(
        CombatantSnapshot combatant,
        int index,
        long tick
    ) {
        PresentationVector position = tacticalPosition(combatant, index, tick);
        PresentationVector nextPosition = tacticalPosition(
            combatant,
            index,
            tick + 1
        );
        PresentationVector velocity = new PresentationVector(
            nextPosition.x() - position.x(),
            nextPosition.y() - position.y(),
            nextPosition.z() - position.z()
        );
        double heading = Math.atan2(velocity.y(), velocity.x());

        return new BattleShipView(
            combatant.participantId(),
            "Ship " + shortId(combatant),
            "UNKNOWN",
            combatant.side().name(),
            position,
            velocity,
            heading,
            combatant.selectedTargetId(),
            combatant.hullPercentage(),
            combatant.shields().percentage(),
            "NONE",
            combatant.status().name(),
            0,
            renderFlags(combatant)
        );
    }

    private static PresentationVector tacticalPosition(
        CombatantSnapshot combatant,
        int index,
        long tick
    ) {
        boolean friendly = combatant.side().name().equals("FRIENDLY");
        int lane = index % 3;
        double direction = friendly ? 1.0 : -1.0;
        double phase = tick * MOTION_STEP + index * 0.9;
        double centerX = friendly ? -260.0 : 260.0;
        double radiusX = 95.0 + lane * 18.0;
        double radiusY = 70.0 + lane * 22.0;

        double x = centerX + Math.cos(phase) * radiusX * direction;
        double y = (lane - 1) * 105.0 + Math.sin(phase) * radiusY;

        return new PresentationVector(x, y, 0.0);
    }


    private java.util.List<BattleProjectileView> mapProjectiles(
        PresentationContext context
    ) {
        return context.projectiles().stream()
            .filter(projectile ->
                projectile.status()
                    == com.beyondsignal.game.combat.projectile.ProjectileStatus.IN_FLIGHT
            )
            .sorted(java.util.Comparator.comparing(
                projectile -> projectile.projectileId().toString()
            ))
            .map(projectile -> new BattleProjectileView(
                projectile.projectileId(),
                projectile.sourceId(),
                projectile.targetId(),
                projectile.weaponId(),
                projectile.status().name(),
                projectile.progress(context.snapshot().tick())
            ))
            .toList();
    }

    private java.util.List<BattleEventView> mapEvents(
        PresentationContext context
    ) {
        return context.combatEvents().stream()
            .sorted(java.util.Comparator.comparingLong(CombatEvent::sequence))
            .map(this::eventView)
            .toList();
    }

    private BattleEventView eventView(CombatEvent event) {
        Map<String, String> attributes = new LinkedHashMap<>(event.payload());
        attributes.put("sequence", Long.toString(event.sequence()));
        attributes.put("sourceId", event.sourceId().toString());
        if (event.targetId() != null) {
            attributes.put("targetId", event.targetId().toString());
        }
        return new BattleEventView(
            event.tick(),
            event.type().name(),
            eventMessage(event),
            attributes
        );
    }

    private static String eventMessage(CombatEvent event) {
        String source = shortId(event.sourceId());
        String target = event.targetId() == null
            ? ""
            : " -> " + shortId(event.targetId());
        return source + " " + event.type().name().replace('_', ' ') + target;
    }

    private Map<String, String> debug(PresentationContext context) {
        Map<String, String> debug = new LinkedHashMap<>();
        debug.put("frameSequence", Long.toString(context.frameSequence()));

        if (context.debugOptions().includeCombatStatus()) {
            long operational = context.snapshot().combatants().stream()
                .filter(combatant ->
                    combatant.status().name().equals("ACTIVE")
                        || combatant.status().name().equals("RETREATING"))
                .count();
            debug.put("operationalCombatants", Long.toString(operational));
        }

        if (context.debugOptions().includeWeaponCounts()) {
            long readyWeapons = context.snapshot().combatants().stream()
                .mapToLong(CombatantSnapshot::readyWeaponCount)
                .sum();
            debug.put("readyWeapons", Long.toString(readyWeapons));
        }

        if (context.debugOptions().includeTargetingMetrics()) {
            long targeted = context.snapshot().combatants().stream()
                .filter(combatant -> combatant.selectedTargetId() != null)
                .count();
            debug.put("combatantsWithTargets", Long.toString(targeted));
        }

        return debug;
    }

    private static Set<String> renderFlags(CombatantSnapshot combatant) {
        if (!combatant.status().name().equals("ACTIVE")) {
            return Set.of("SELECTABLE", "STATUS_" + combatant.status().name());
        }
        return Set.of("SELECTABLE");
    }

    private static String shortId(CombatantSnapshot combatant) {
        return shortId(combatant.participantId());
    }

    private static String shortId(java.util.UUID id) {
        return id.toString().substring(0, 8);
    }
}
