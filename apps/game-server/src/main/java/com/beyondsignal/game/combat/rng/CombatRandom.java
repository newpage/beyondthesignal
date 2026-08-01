package com.beyondsignal.game.combat.rng;

public interface CombatRandom {
    long nextLong();
    int nextInt(int bound);
    double nextDouble();
    boolean chance(double probability);
    long state();
}
