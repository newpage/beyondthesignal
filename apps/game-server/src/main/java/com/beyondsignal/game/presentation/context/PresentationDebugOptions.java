package com.beyondsignal.game.presentation.context;

public record PresentationDebugOptions(
    boolean includeCombatStatus,
    boolean includeWeaponCounts,
    boolean includeTargetingMetrics
) {
    public static PresentationDebugOptions disabled() {
        return new PresentationDebugOptions(false, false, false);
    }

    public static PresentationDebugOptions development() {
        return new PresentationDebugOptions(true, true, true);
    }
}
