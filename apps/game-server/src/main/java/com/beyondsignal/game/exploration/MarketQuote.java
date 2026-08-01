package com.beyondsignal.game.exploration;

public record MarketQuote(ResourceType resource, int buyPrice, int sellPrice, int availableUnits) {
    public MarketQuote {
        if (buyPrice < 1 || sellPrice < 0 || availableUnits < 0) {
            throw new IllegalArgumentException("Invalid market quote");
        }
    }
}
