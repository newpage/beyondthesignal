package com.beyondsignal.game.combat.ai.fleet.formation;

import com.beyondsignal.game.combat.ai.fleet.FleetRole;
import java.util.List;

public final class FormationTemplates {
    private FormationTemplates() {
    }

    public static FormationTemplate create(FormationType type, double spacing) {
        return switch (type) {
            case LINE_AHEAD -> lineAhead(spacing);
            case LINE_ABREAST -> lineAbreast(spacing);
            case WEDGE -> wedge(spacing);
            case DIAMOND -> diamond(spacing);
            case ECHELON_LEFT -> echelonLeft(spacing);
            case ECHELON_RIGHT -> echelonRight(spacing);
            case SCREEN -> screen(spacing);
            case DEFENSIVE_RING -> defensiveRing(spacing);
        };
    }

    private static FormationTemplate lineAhead(double spacing) {
        return template(
            FormationType.LINE_AHEAD,
            spacing,
            slot(0, "leader", 0, 0, 0, FleetRole.COMMANDER, 100),
            slot(1, "trail-1", 0, 0, -1, FleetRole.ESCORT, 90),
            slot(2, "trail-2", 0, 0, -2, FleetRole.STRIKE, 80),
            slot(3, "trail-3", 0, 0, -3, FleetRole.SUPPORT, 70)
        );
    }

    private static FormationTemplate lineAbreast(double spacing) {
        return template(
            FormationType.LINE_ABREAST,
            spacing,
            slot(0, "leader", 0, 0, 0, FleetRole.COMMANDER, 100),
            slot(1, "left", -1, 0, 0, FleetRole.ESCORT, 90),
            slot(2, "right", 1, 0, 0, FleetRole.ESCORT, 90),
            slot(3, "far-left", -2, 0, 0, FleetRole.STRIKE, 70),
            slot(4, "far-right", 2, 0, 0, FleetRole.STRIKE, 70)
        );
    }

    private static FormationTemplate wedge(double spacing) {
        return template(
            FormationType.WEDGE,
            spacing,
            slot(0, "leader", 0, 0, 0, FleetRole.COMMANDER, 100),
            slot(1, "left-1", -1, 0, -1, FleetRole.ESCORT, 90),
            slot(2, "right-1", 1, 0, -1, FleetRole.ESCORT, 90),
            slot(3, "left-2", -2, 0, -2, FleetRole.STRIKE, 75),
            slot(4, "right-2", 2, 0, -2, FleetRole.STRIKE, 75)
        );
    }

    private static FormationTemplate diamond(double spacing) {
        return template(
            FormationType.DIAMOND,
            spacing,
            slot(0, "leader", 0, 0, 0, FleetRole.COMMANDER, 100),
            slot(1, "left", -1, 0, -1, FleetRole.ESCORT, 90),
            slot(2, "right", 1, 0, -1, FleetRole.ESCORT, 90),
            slot(3, "rear", 0, 0, -2, FleetRole.SUPPORT, 80)
        );
    }

    private static FormationTemplate echelonLeft(double spacing) {
        return template(
            FormationType.ECHELON_LEFT,
            spacing,
            slot(0, "leader", 0, 0, 0, FleetRole.COMMANDER, 100),
            slot(1, "left-1", -1, 0, -1, FleetRole.ESCORT, 90),
            slot(2, "left-2", -2, 0, -2, FleetRole.STRIKE, 80),
            slot(3, "left-3", -3, 0, -3, FleetRole.SUPPORT, 70)
        );
    }

    private static FormationTemplate echelonRight(double spacing) {
        return template(
            FormationType.ECHELON_RIGHT,
            spacing,
            slot(0, "leader", 0, 0, 0, FleetRole.COMMANDER, 100),
            slot(1, "right-1", 1, 0, -1, FleetRole.ESCORT, 90),
            slot(2, "right-2", 2, 0, -2, FleetRole.STRIKE, 80),
            slot(3, "right-3", 3, 0, -3, FleetRole.SUPPORT, 70)
        );
    }

    private static FormationTemplate screen(double spacing) {
        return template(
            FormationType.SCREEN,
            spacing,
            slot(0, "leader", 0, 0, 0, FleetRole.COMMANDER, 100),
            slot(1, "screen-left", -2, 0, 1, FleetRole.SCOUT, 95),
            slot(2, "screen-center", 0, 0, 2, FleetRole.SCOUT, 95),
            slot(3, "screen-right", 2, 0, 1, FleetRole.SCOUT, 95),
            slot(4, "rear-support", 0, 0, -2, FleetRole.SUPPORT, 70)
        );
    }

    private static FormationTemplate defensiveRing(double spacing) {
        return template(
            FormationType.DEFENSIVE_RING,
            spacing,
            slot(0, "leader", 0, 0, 0, FleetRole.COMMANDER, 100),
            slot(1, "front", 0, 0, 1, FleetRole.ESCORT, 95),
            slot(2, "right", 1, 0, 0, FleetRole.ESCORT, 95),
            slot(3, "rear", 0, 0, -1, FleetRole.ESCORT, 95),
            slot(4, "left", -1, 0, 0, FleetRole.ESCORT, 95),
            slot(5, "upper", 0, 1, 0, FleetRole.SUPPORT, 80),
            slot(6, "lower", 0, -1, 0, FleetRole.SUPPORT, 80)
        );
    }

    private static FormationTemplate template(
        FormationType type,
        double spacing,
        FormationSlot... slots
    ) {
        return new FormationTemplate(type, spacing, List.of(slots));
    }

    private static FormationSlot slot(
        int index,
        String name,
        double x,
        double y,
        double z,
        FleetRole role,
        int priority
    ) {
        return new FormationSlot(
            index,
            name,
            new FormationVector(x, y, z),
            role,
            priority
        );
    }
}
