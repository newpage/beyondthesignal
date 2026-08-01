package com.beyondsignal.game.exploration;

import java.util.Objects;

public final class TradeService {
    public ShipResources buy(
        CargoHold cargo,
        ShipResources resources,
        MarketQuote quote,
        int quantity
    ) {
        Objects.requireNonNull(cargo, "cargo");
        Objects.requireNonNull(resources, "resources");
        Objects.requireNonNull(quote, "quote");
        if (quantity < 1 || quantity > quote.availableUnits()) {
            throw new IllegalArgumentException("Invalid quantity");
        }
        int cost = Math.multiplyExact(quote.buyPrice(), quantity);
        if (cost > resources.credits()) {
            throw new IllegalStateException("Insufficient credits");
        }
        cargo.add(quote.resource(), quantity);
        return resources.debit(cost);
    }

    public ShipResources sell(
        CargoHold cargo,
        ShipResources resources,
        MarketQuote quote,
        int quantity
    ) {
        Objects.requireNonNull(cargo, "cargo");
        Objects.requireNonNull(resources, "resources");
        Objects.requireNonNull(quote, "quote");
        if (quantity < 1) {
            throw new IllegalArgumentException("Invalid quantity");
        }
        cargo.remove(quote.resource(), quantity);
        return resources.credit(Math.multiplyExact(quote.sellPrice(), quantity));
    }
}
