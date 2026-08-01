package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class MovementExecutionEngine {
    private final HeadingController headingController;
    private final AccelerationController accelerationController;

    public MovementExecutionEngine() {
        this(new HeadingController(), new AccelerationController());
    }

    public MovementExecutionEngine(
        HeadingController headingController,
        AccelerationController accelerationController
    ) {
        this.headingController = Objects.requireNonNull(
            headingController, "headingController"
        );
        this.accelerationController = Objects.requireNonNull(
            accelerationController, "accelerationController"
        );
    }

    public MovementExecutionStep execute(MovementExecutionContext context) {
        Objects.requireNonNull(context, "context");

        CombatVector positionDelta = context.desiredPosition()
            .subtract(context.currentPosition());
        CombatVector desiredHeading = headingFor(
            positionDelta,
            context.desiredVelocity(),
            context.currentHeading()
        );

        double headingError = headingController.headingErrorDegrees(
            context.currentHeading(),
            desiredHeading
        );
        CombatVector commandedHeading = headingController.turnToward(
            context.currentHeading(),
            desiredHeading,
            context.limits().maximumTurnRateDegrees()
        );

        double desiredSpeed = Math.min(
            context.limits().maximumSpeed(),
            context.desiredVelocity().magnitude()
        );
        double acceleration = accelerationController.command(
            context.currentVelocity().magnitude(),
            desiredSpeed,
            context.limits().maximumAcceleration()
        );

        MovementExecutionState state = state(context, headingError, acceleration);
        double progress = progress(context);

        Map<String, Integer> reasons = new LinkedHashMap<>();
        reasons.put("headingError", (int) Math.round(Math.min(45.0, headingError)));
        reasons.put(
            "positionError",
            (int) Math.round(Math.min(35.0, context.positionError()))
        );
        reasons.put(
            "velocityError",
            (int) Math.round(Math.min(20.0, context.velocityError()))
        );

        return new MovementExecutionStep(
            context.participantId(),
            context.tick(),
            state,
            new MovementCommand(commandedHeading, acceleration, desiredSpeed),
            progress,
            reasons
        );
    }

    private static CombatVector headingFor(
        CombatVector positionDelta,
        CombatVector desiredVelocity,
        CombatVector fallback
    ) {
        if (positionDelta.magnitude() > 0.0) {
            return positionDelta.normalize();
        }
        if (desiredVelocity.magnitude() > 0.0) {
            return desiredVelocity.normalize();
        }
        return fallback.normalize();
    }

    private static MovementExecutionState state(
        MovementExecutionContext context,
        double headingError,
        double acceleration
    ) {
        if (context.positionError() <= context.limits().positionTolerance()
            && context.velocityError() <= context.limits().velocityTolerance()) {
            return MovementExecutionState.COMPLETED;
        }
        if (headingError > context.limits().maximumTurnRateDegrees()) {
            return MovementExecutionState.TURNING;
        }
        if (Math.abs(acceleration) > 0.000001) {
            return MovementExecutionState.ACCELERATING;
        }
        if (context.positionError() <= context.limits().positionTolerance() * 3.0) {
            return MovementExecutionState.CORRECTING;
        }
        return MovementExecutionState.CRUISING;
    }

    private static double progress(MovementExecutionContext context) {
        double tolerance = Math.max(context.limits().positionTolerance(), 1.0);
        return Math.max(
            0.0,
            Math.min(1.0, tolerance / Math.max(context.positionError(), tolerance))
        );
    }
}
