package com.beyondsignal.game.combat.ai.maneuver;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class ManeuverScorer {
    public ManeuverScore score(
        ManeuverType type,
        ManeuverContext context,
        ManeuverSelectionPolicy policy
    ) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(policy, "policy");

        if (!context.constraints().allows(type)) {
            return new ManeuverScore(
                type,
                -1000,
                Map.of("constraintViolation", -1000)
            );
        }

        Map<String, Integer> factors = new LinkedHashMap<>();
        double distance = context.normalizedDistance();

        factors.put("objective", objectiveScore(type, context.objective()));
        factors.put("range", rangeScore(type, distance, policy.rangePreference()));
        factors.put("survival", survivalScore(type, context, policy.caution()));
        factors.put("aggression", aggressionScore(type, policy.aggression()));
        factors.put(
            "formation",
            formationScore(type, context.formationIntegrity(), policy.formationDiscipline())
        );
        factors.put(
            "position",
            positionalScore(type, context.friendlySupport(), policy.positionalPreference())
        );

        int total = factors.values().stream().mapToInt(Integer::intValue).sum();
        return new ManeuverScore(type, total, factors);
    }

    private static int objectiveScore(
        ManeuverType type,
        ManeuverObjective objective
    ) {
        return switch (objective) {
            case CLOSE_RANGE -> switch (type) {
                case ADVANCE, PURSUE, INTERCEPT -> 35;
                default -> 0;
            };
            case OPEN_RANGE, DISENGAGE -> switch (type) {
                case WITHDRAW, KITE -> 40;
                default -> 0;
            };
            case MAINTAIN_RANGE -> switch (type) {
                case HOLD_DISTANCE, ORBIT, KITE -> 30;
                default -> 0;
            };
            case GAIN_POSITIONAL_ADVANTAGE -> switch (type) {
                case FLANK_LEFT, FLANK_RIGHT, ORBIT, INTERCEPT -> 35;
                default -> 0;
            };
            case PROTECT_ALLY -> switch (type) {
                case INTERCEPT, MAINTAIN_POSITION, HOLD_DISTANCE -> 30;
                default -> 0;
            };
            case HOLD_GROUND -> switch (type) {
                case MAINTAIN_POSITION, HOLD_DISTANCE -> 35;
                default -> 0;
            };
        };
    }

    private static int rangeScore(
        ManeuverType type,
        double normalizedDistance,
        double preference
    ) {
        int weight = (int) Math.round(preference * 25.0);

        if (normalizedDistance > 1.20) {
            return switch (type) {
                case ADVANCE, PURSUE, INTERCEPT -> weight;
                case WITHDRAW, KITE -> -weight;
                default -> 0;
            };
        }
        if (normalizedDistance < 0.65) {
            return switch (type) {
                case WITHDRAW, KITE, HOLD_DISTANCE -> weight;
                case ADVANCE, PURSUE -> -weight;
                default -> 0;
            };
        }
        return switch (type) {
            case HOLD_DISTANCE, ORBIT, MAINTAIN_POSITION -> weight;
            default -> 0;
        };
    }

    private static int survivalScore(
        ManeuverType type,
        ManeuverContext context,
        double caution
    ) {
        double risk = Math.max(
            1.0 - context.hullPercentage(),
            Math.max(1.0 - context.shieldPercentage(), context.incomingThreat())
        );
        int weight = (int) Math.round(risk * caution * 40.0);

        return switch (type) {
            case WITHDRAW, KITE, HOLD_DISTANCE -> weight;
            case ADVANCE, PURSUE, INTERCEPT -> -weight / 2;
            default -> 0;
        };
    }

    private static int aggressionScore(
        ManeuverType type,
        double aggression
    ) {
        int weight = (int) Math.round(aggression * 25.0);
        return switch (type) {
            case ADVANCE, PURSUE, INTERCEPT, FLANK_LEFT, FLANK_RIGHT -> weight;
            case WITHDRAW, KITE -> -weight / 2;
            default -> 0;
        };
    }

    private static int formationScore(
        ManeuverType type,
        double integrity,
        double discipline
    ) {
        int disciplineWeight = (int) Math.round(discipline * 20.0);

        if (integrity < 0.50) {
            return switch (type) {
                case MAINTAIN_POSITION, HOLD_DISTANCE -> disciplineWeight;
                case FLANK_LEFT, FLANK_RIGHT, ORBIT -> -disciplineWeight;
                default -> 0;
            };
        }

        return switch (type) {
            case MAINTAIN_POSITION, HOLD_DISTANCE -> disciplineWeight / 2;
            default -> 0;
        };
    }

    private static int positionalScore(
        ManeuverType type,
        double friendlySupport,
        double preference
    ) {
        int weight = (int) Math.round(preference * 20.0);

        if (friendlySupport >= 0.60) {
            return switch (type) {
                case FLANK_LEFT, FLANK_RIGHT, ORBIT, ADVANCE -> weight;
                default -> 0;
            };
        }

        return switch (type) {
            case MAINTAIN_POSITION, WITHDRAW, KITE -> weight / 2;
            default -> 0;
        };
    }
}
