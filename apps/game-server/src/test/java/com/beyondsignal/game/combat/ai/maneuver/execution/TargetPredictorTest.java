package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import org.junit.jupiter.api.Test;

class TargetPredictorTest {
    @Test
    void predictsLinearTargetMotion() {
        MovementPrediction prediction = new TargetPredictor().predict(
            new CombatVector(100, 0, 0),
            new CombatVector(10, 5, 0),
            4.0
        );

        assertEquals(
            new CombatVector(140, 20, 0),
            prediction.predictedPosition()
        );
        assertEquals(4.0, prediction.horizonSeconds());
    }
}
