package com.beyondsignal.game.combat.scenario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.replay.ReplayValidator;
import com.beyondsignal.game.combat.weapon.DamageType;
import com.beyondsignal.game.combat.weapon.WeaponArc;
import com.beyondsignal.game.combat.weapon.WeaponDefinition;
import com.beyondsignal.game.combat.weapon.WeaponType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatScenarioRunnerTest {
    @Test
    void repeatedScenarioProducesIdenticalReplay() {
        CombatScenario scenario = scenario();

        CombatScenarioResult first = new CombatScenarioRunner().run(scenario);
        CombatScenarioResult second = new CombatScenarioRunner().run(scenario);

        assertEquals(first.replay().checksum(), second.replay().checksum());
        assertEquals(first.statistics(), second.statistics());
        assertTrue(new ReplayValidator().valid(first.replay()));
        assertTrue(first.ticksExecuted() > 0);
        assertTrue(first.statistics().weaponsFired() > 0);
    }

    private static CombatScenario scenario() {
        WeaponDefinition weapon = new WeaponDefinition(
            "phaser-mk1",
            "Phaser Mk I",
            WeaponType.BEAM,
            DamageType.ENERGY,
            WeaponArc.FORWARD,
            25,
            10000,
            0.9,
            0,
            2,
            0
        );

        return new CombatScenario(
            CombatId.fromString("00000000-0000-0000-0000-000000000500"),
            12345L,
            25,
            1000.0,
            List.of(
                new CombatScenarioParticipant(
                    UUID.fromString("00000000-0000-0000-0000-000000000501"),
                    CombatSide.FRIENDLY,
                    100,
                    50,
                    0.9,
                    0.1,
                    List.of(weapon)
                ),
                new CombatScenarioParticipant(
                    UUID.fromString("00000000-0000-0000-0000-000000000502"),
                    CombatSide.HOSTILE,
                    100,
                    50,
                    0.8,
                    0.2,
                    List.of(weapon)
                )
            )
        );
    }
}
