package com.beyondsignal.game.combat.rng;

public final class SplitMix64CombatRandom implements CombatRandom {
    private long state;

    public SplitMix64CombatRandom(long seed) {
        this.state = seed;
    }

    public static SplitMix64CombatRandom restore(long state) {
        return new SplitMix64CombatRandom(state);
    }

    @Override
    public long nextLong() {
        long z = (state += 0x9E3779B97F4A7C15L);
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    @Override
    public int nextInt(int bound) {
        if (bound <= 0) throw new IllegalArgumentException("bound must be positive");
        return (int) Long.remainderUnsigned(nextLong(), bound);
    }

    @Override
    public double nextDouble() {
        return (nextLong() >>> 11) * 0x1.0p-53;
    }

    @Override
    public boolean chance(double probability) {
        if (!Double.isFinite(probability) || probability < 0.0 || probability > 1.0) {
            throw new IllegalArgumentException("probability must be between 0.0 and 1.0");
        }
        return nextDouble() < probability;
    }

    @Override
    public long state() {
        return state;
    }
}
