package com.beyondsignal.game.combat.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.combat.fleet.FleetDoctrine;
import com.beyondsignal.game.combat.fleet.FleetState;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.rng.SplitMix64CombatRandom;
import com.beyondsignal.game.combat.shield.ShieldModel;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatEncounterFleetTest {
    @Test
    void registersAndReturnsFleetsInInsertionOrder() {
        CombatEncounter encounter = encounter();
        FleetState first = fleet("Alliance", CombatSide.FRIENDLY);
        FleetState second = fleet("Hostile", CombatSide.HOSTILE);

        encounter.addFleet(first);
        encounter.addFleet(second);

        assertEquals(List.of(first, second), encounter.fleets());
        assertSame(first, encounter.fleet(first.fleetId()).orElseThrow());
    }

    @Test
    void rejectsDuplicateFleetIds() {
        CombatEncounter encounter = encounter();
        UUID id = UUID.randomUUID();

        encounter.addFleet(fleet(id, "Primary", CombatSide.FRIENDLY));

        assertThrows(
            IllegalArgumentException.class,
            () -> encounter.addFleet(
                fleet(id, "Duplicate", CombatSide.FRIENDLY)
            )
        );
    }

    @Test
    void rejectsFleetRegistrationAfterEncounterStarts() {
        CombatEncounter encounter = encounter();
        encounter.addParticipant(participant(CombatSide.FRIENDLY));
        encounter.addParticipant(participant(CombatSide.HOSTILE));
        encounter.start();

        assertThrows(
            IllegalStateException.class,
            () -> encounter.addFleet(
                fleet("Late Fleet", CombatSide.FRIENDLY)
            )
        );
    }

    @Test
    void unknownFleetLookupIsEmpty() {
        assertTrue(encounter().fleet(UUID.randomUUID()).isEmpty());
    }

    private static CombatEncounter encounter() {
        CombatId combatId = CombatId.random();
        return new CombatEncounter(new CombatContext(
            combatId,
            42L,
            new SplitMix64CombatRandom(42L),
            new CombatClock()
        ));
    }

    private static CombatParticipant participant(CombatSide side) {
        return new CombatParticipant(
            UUID.randomUUID(),
            side,
            ShieldModel.uniform(100, 0),
            List.of(),
            100,
            0.5,
            0.0
        );
    }

    private static FleetState fleet(String name, CombatSide side) {
        return fleet(UUID.randomUUID(), name, side);
    }

    private static FleetState fleet(
        UUID id,
        String name,
        CombatSide side
    ) {
        return new FleetState(
            id,
            name,
            "Commander",
            side,
            FleetDoctrine.DEFENSIVE,
            List.of(),
            List.of()
        );
    }
}
