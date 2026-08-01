package com.beyondsignal.game.exploration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class DockingAndStarbaseServiceTest {
    @Test
    void docksRepairsAndRefuelsShip() {
        DockingController docking = new DockingController();
        docking.requestDocking("system-1", true, 0.2);
        docking.completeDocking();
        assertEquals(DockingStatus.DOCKED, docking.status());

        ShipResources damaged = ShipResources.initial()
            .consumeFuel(40)
            .damage(30);

        StarbaseService service = new StarbaseService();
        ShipResources serviced = service.repair(service.refuel(damaged, 20), 10);

        assertEquals(80, serviced.fuel());
        assertEquals(80, serviced.hullIntegrity());
        assertEquals(860, serviced.credits());
    }

    @Test
    void rejectsDockingOutsideRange() {
        assertThrows(
            IllegalStateException.class,
            () -> new DockingController().requestDocking("system-1", true, 2.5)
        );
    }
}
