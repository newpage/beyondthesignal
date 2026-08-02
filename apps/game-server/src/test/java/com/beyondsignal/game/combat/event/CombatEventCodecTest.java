package com.beyondsignal.game.combat.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.beyondsignal.game.combat.model.CombatId;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatEventCodecTest {
    @Test
    void roundTripsEventBatch() {
        CombatId id = CombatId.fromString(
            "00000000-0000-0000-0000-000000000901"
        );
        CombatEventBatch batch = new CombatEventBatch(
            id,
            7,
            List.of(new CombatEvent(
                id,
                1,
                7,
                UUID.fromString("00000000-0000-0000-0000-000000000902"),
                UUID.fromString("00000000-0000-0000-0000-000000000903"),
                CombatEventType.PROJECTILE_IMPACT,
                Instant.EPOCH.plusSeconds(7),
                Map.of("damage", "25")
            ))
        );

        CombatEventCodec codec = new CombatEventCodec();
        assertEquals(batch, codec.decode(codec.encode(batch)));
    }
}
