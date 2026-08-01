package com.beyondsignal.game.combat.ai.maneuver;

import java.util.Set;

public record ManeuverConstraints(
    Set<ManeuverType> prohibited,
    boolean mustMaintainFormation,
    boolean cannotAdvance,
    boolean cannotWithdraw
) {
    public ManeuverConstraints {
        prohibited = Set.copyOf(prohibited);
    }

    public boolean allows(ManeuverType type) {
        if (prohibited.contains(type)) {
            return false;
        }
        if (cannotAdvance && (type == ManeuverType.ADVANCE
            || type == ManeuverType.PURSUE
            || type == ManeuverType.INTERCEPT)) {
            return false;
        }
        if (cannotWithdraw && (type == ManeuverType.WITHDRAW
            || type == ManeuverType.KITE)) {
            return false;
        }
        if (mustMaintainFormation && (type == ManeuverType.FLANK_LEFT
            || type == ManeuverType.FLANK_RIGHT
            || type == ManeuverType.ORBIT)) {
            return false;
        }
        return true;
    }

    public static ManeuverConstraints unrestricted() {
        return new ManeuverConstraints(Set.of(), false, false, false);
    }
}
