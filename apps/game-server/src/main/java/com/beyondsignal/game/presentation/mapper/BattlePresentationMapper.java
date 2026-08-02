package com.beyondsignal.game.presentation.mapper;

import com.beyondsignal.game.combat.ai.snapshot.CombatantSnapshot;
import com.beyondsignal.game.presentation.context.PresentationContext;
import com.beyondsignal.game.presentation.frame.BattleFrameMetadata;
import com.beyondsignal.game.presentation.frame.BattleFrameV1;
import com.beyondsignal.game.presentation.frame.BattleShipView;
import com.beyondsignal.game.presentation.frame.PresentationVector;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class BattlePresentationMapper {
    private static final PresentationVector ZERO_VECTOR =
        new PresentationVector(0.0, 0.0, 0.0);

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
            context.snapshot().combatants().stream()
                .sorted((left, right) ->
                    left.participantId().compareTo(right.participantId()))
                .map(this::ship)
                .toList(),
            java.util.List.of(),
            java.util.List.of(),
            java.util.List.of(),
            debug(context)
        );
    }

    private BattleShipView ship(CombatantSnapshot combatant) {
        return new BattleShipView(
            combatant.participantId(),
            "Ship " + shortId(combatant),
            "UNKNOWN",
            combatant.side().name(),
            ZERO_VECTOR,
            ZERO_VECTOR,
            0.0,
            combatant.selectedTargetId(),
            combatant.hullPercentage(),
            combatant.shields().percentage(),
            "NONE",
            combatant.status().name(),
            0,
            renderFlags(combatant)
        );
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
        return combatant.participantId().toString().substring(0, 8);
    }
}
