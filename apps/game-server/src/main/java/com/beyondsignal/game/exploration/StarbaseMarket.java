package com.beyondsignal.game.exploration;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public final class StarbaseMarket {
    private final EnumMap<ResourceType, MarketQuote> quotes = new EnumMap<>(ResourceType.class);

    public StarbaseMarket(long seed) {
        Random random = new Random(seed);
        for (ResourceType type : ResourceType.values()) {
            int base = switch (type) {
                case FUEL -> 8;
                case SUPPLIES -> 12;
                case MEDICAL -> 25;
                case RESEARCH_DATA -> 40;
                case INDUSTRIAL_COMPONENTS -> 18;
            };
            int buy = Math.max(1, base + random.nextInt(7) - 3);
            int sell = Math.max(1, buy - 2 - random.nextInt(3));
            quotes.put(type, new MarketQuote(type, buy, sell, 20 + random.nextInt(81)));
        }
    }

    public MarketQuote quote(ResourceType resource) {
        return quotes.get(resource);
    }

    public Map<ResourceType, MarketQuote> quotes() {
        return Map.copyOf(quotes);
    }
}
