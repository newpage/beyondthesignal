package com.beyondsignal.game.exploration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class TradeServiceTest {
    @Test
    void buysAndSellsCargo() {
        CargoHold cargo = new CargoHold(20);
        ShipResources resources = ShipResources.initial();
        MarketQuote quote = new MarketQuote(ResourceType.SUPPLIES, 10, 7, 10);
        TradeService service = new TradeService();

        resources = service.buy(cargo, resources, quote, 5);
        assertEquals(5, cargo.quantity(ResourceType.SUPPLIES));
        assertEquals(950, resources.credits());

        resources = service.sell(cargo, resources, quote, 2);
        assertEquals(3, cargo.quantity(ResourceType.SUPPLIES));
        assertEquals(964, resources.credits());
    }

    @Test
    void rejectsPurchaseBeyondCargoCapacity() {
        CargoHold cargo = new CargoHold(2);
        assertThrows(
            IllegalStateException.class,
            () -> new TradeService().buy(
                cargo,
                ShipResources.initial(),
                new MarketQuote(ResourceType.FUEL, 5, 3, 20),
                3
            )
        );
    }
}
