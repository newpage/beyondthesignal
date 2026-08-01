package com.beyondsignal.game.combat.ai.fleet;

import com.beyondsignal.game.combat.ai.snapshot.CombatSnapshotFactory;
import com.beyondsignal.game.combat.engine.CombatEncounter;
import java.util.Objects;

public final class FleetSnapshotFactory {
    private final CombatSnapshotFactory combatSnapshotFactory;

    public FleetSnapshotFactory() {
        this(new CombatSnapshotFactory());
    }

    public FleetSnapshotFactory(CombatSnapshotFactory combatSnapshotFactory) {
        this.combatSnapshotFactory =
            Objects.requireNonNull(combatSnapshotFactory, "combatSnapshotFactory");
    }

    public FleetSnapshot create(
        FleetDefinition fleet,
        CombatEncounter encounter
    ) {
        Objects.requireNonNull(fleet, "fleet");
        Objects.requireNonNull(encounter, "encounter");
        return new FleetSnapshot(
            fleet.fleetId(),
            fleet.doctrine(),
            fleet.members(),
            combatSnapshotFactory.create(encounter)
        );
    }
}
