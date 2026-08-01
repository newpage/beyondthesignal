package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.beyondsignal.game.combat.ai.fleet.FleetRole;
import java.util.List;
import org.junit.jupiter.api.Test;

class FormationTemplateTest {
    @Test
    void wedgeTemplateHasStableSlots() {
        FormationTemplate template =
            FormationTemplates.create(FormationType.WEDGE, 1000.0);

        assertEquals(FormationType.WEDGE, template.type());
        assertEquals(5, template.slots().size());
        assertEquals("leader", template.slot(0).name());
        assertEquals("left-1", template.slot(1).name());
        assertEquals("right-1", template.slot(2).name());
    }

    @Test
    void rejectsDuplicateSlotIndexes() {
        FormationSlot first = new FormationSlot(
            0, "first", FormationVector.zero(), FleetRole.COMMANDER, 100
        );
        FormationSlot duplicate = new FormationSlot(
            0, "second", new FormationVector(1, 0, 0), FleetRole.ESCORT, 90
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new FormationTemplate(
                FormationType.LINE_AHEAD,
                1000.0,
                List.of(first, duplicate)
            )
        );
    }
}
