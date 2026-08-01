package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.rng.CombatRandom;
import com.beyondsignal.game.combat.weapon.WeaponAccuracyModel;
import com.beyondsignal.game.combat.weapon.WeaponMountState;
import java.util.Objects;

public final class FireControlComputer {
    private final WeaponAccuracyModel accuracyModel;

    public FireControlComputer() {
        this(new WeaponAccuracyModel());
    }

    public FireControlComputer(WeaponAccuracyModel accuracyModel) {
        this.accuracyModel = Objects.requireNonNull(accuracyModel, "accuracyModel");
    }

    public FireSolution calculate(
        CombatParticipant attacker,
        CombatParticipant target,
        WeaponMountState weapon,
        double distance,
        double powerModifier,
        CombatRandom random
    ) {
        Objects.requireNonNull(attacker, "attacker");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(weapon, "weapon");
        Objects.requireNonNull(random, "random");

        if (!weapon.readyToFire()) {
            throw new IllegalStateException("Weapon mount is not ready");
        }

        double probability = accuracyModel.hitProbability(
            weapon.definition(),
            distance,
            attacker.targetingQuality(),
            target.evasion(),
            powerModifier
        );
        double roll = random.nextDouble();

        return new FireSolution(
            attacker.participantId(),
            target.participantId(),
            weapon,
            distance,
            probability,
            roll,
            roll < probability
        );
    }
}
