package com.beyondsignal.game.combat.ai.snapshot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.engine.CombatClock;
import com.beyondsignal.game.combat.engine.CombatContext;
import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.engine.CombatParticipant;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.rng.SplitMix64CombatRandom;
import com.beyondsignal.game.combat.shield.ShieldModel;
import com.beyondsignal.game.combat.weapon.DamageType;
import com.beyondsignal.game.combat.weapon.WeaponArc;
import com.beyondsignal.game.combat.weapon.WeaponDefinition;
import com.beyondsignal.game.combat.weapon.WeaponMountState;
import com.beyondsignal.game.combat.weapon.WeaponType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatSnapshotFactoryTest {
    @Test
    void createsImmutableAiSnapshotFromEncounter() {
        CombatId id = CombatId.random();
        CombatEncounter encounter = new CombatEncounter(new CombatContext(
            id, 7L, new SplitMix64CombatRandom(7L), new CombatClock(12L)
        ));
        UUID participantId = UUID.randomUUID();
        encounter.addParticipant(new CombatParticipant(
            participantId,
            CombatSide.FRIENDLY,
            ShieldModel.uniform(50, 2),
            List.of(WeaponMountState.ready(UUID.randomUUID(), weapon(), 0)),
            100,
            0.8,
            0.2
        ));

        CombatAiSnapshot snapshot = new CombatSnapshotFactory().create(encounter);

        assertEquals(id, snapshot.combatId());
        assertEquals(12L, snapshot.tick());
        assertEquals(1, snapshot.combatants().size());
        assertTrue(snapshot.combatant(participantId).orElseThrow().weapons().getFirst().ready());
    }

    private static WeaponDefinition weapon() {
        return new WeaponDefinition(
            "phaser", "Phaser", WeaponType.BEAM, DamageType.ENERGY,
            WeaponArc.FORWARD, 25, 10000, 0.8, 0, 3, 0
        );
    }
}
