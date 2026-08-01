package com.beyondsignal.game.combat.weapon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class WeaponAccuracyModelTest {
    private final WeaponAccuracyModel model = new WeaponAccuracyModel();
    private final WeaponDefinition weapon = new WeaponDefinition(
        "phaser",
        "Phaser",
        WeaponType.BEAM,
        DamageType.ENERGY,
        WeaponArc.FORWARD,
        25,
        10000,
        0.8,
        0,
        5,
        0
    );

    @Test
    void rejectsTargetsOutsideRange() {
        assertEquals(0.0, model.hitProbability(weapon, 10001, 1.0, 0.0, 1.0));
    }

    @Test
    void evasionReducesHitProbability() {
        double stationary = model.hitProbability(weapon, 1000, 1.0, 0.0, 1.0);
        double evasive = model.hitProbability(weapon, 1000, 1.0, 1.0, 1.0);
        assertTrue(evasive < stationary);
    }
}
