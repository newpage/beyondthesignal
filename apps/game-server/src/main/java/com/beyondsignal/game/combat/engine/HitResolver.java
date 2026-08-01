package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.rng.CombatRandom;
import com.beyondsignal.game.combat.weapon.WeaponAccuracyModel;
import com.beyondsignal.game.combat.weapon.WeaponDefinition;
import java.util.Objects;

public final class HitResolver {
    private final WeaponAccuracyModel accuracyModel;

    public HitResolver() {
        this(new WeaponAccuracyModel());
    }

    public HitResolver(WeaponAccuracyModel accuracyModel) {
        this.accuracyModel = Objects.requireNonNull(accuracyModel, "accuracyModel");
    }

    public HitResolution resolve(
        WeaponDefinition weapon,
        double distance,
        double targetingQuality,
        double evasion,
        double powerModifier,
        CombatRandom random
    ) {
        Objects.requireNonNull(random, "random");
        double probability = accuracyModel.hitProbability(
            weapon,
            distance,
            targetingQuality,
            evasion,
            powerModifier
        );
        double roll = random.nextDouble();
        return new HitResolution(probability, roll, roll < probability);
    }
}
