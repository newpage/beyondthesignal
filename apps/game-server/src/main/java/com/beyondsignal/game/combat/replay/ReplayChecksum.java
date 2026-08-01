package com.beyondsignal.game.combat.replay;

import com.beyondsignal.game.combat.event.CombatEvent;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

public final class ReplayChecksum {
    public String calculate(List<CombatReplayFrame> frames) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (CombatReplayFrame frame : frames) {
                update(digest, Long.toString(frame.tick()));
                for (CombatEvent event : frame.events()) {
                    update(digest, event.combatId().toString());
                    update(digest, Long.toString(event.sequence()));
                    update(digest, Long.toString(event.tick()));
                    update(digest, event.sourceId().toString());
                    update(digest, event.targetId() == null ? "" : event.targetId().toString());
                    update(digest, event.type().name());
                    event.payload().entrySet().stream()
                        .sorted(java.util.Map.Entry.comparingByKey())
                        .forEach(entry -> {
                            update(digest, entry.getKey());
                            update(digest, entry.getValue());
                        });
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }

    private static void update(MessageDigest digest, String value) {
        digest.update(value.getBytes(StandardCharsets.UTF_8));
        digest.update((byte) 0);
    }
}
