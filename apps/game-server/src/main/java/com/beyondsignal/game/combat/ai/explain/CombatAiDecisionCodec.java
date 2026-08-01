package com.beyondsignal.game.combat.ai.explain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

public final class CombatAiDecisionCodec {
    public String canonical(CombatAiDecisionRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append(record.participantId()).append('|')
            .append(record.tick()).append('|')
            .append(record.goalType()).append('|')
            .append(record.targetId() == null ? "" : record.targetId()).append('|')
            .append(record.confidence().percentage()).append('|');

        record.reasons().forEach(reason -> builder
            .append(reason.code()).append(':')
            .append(reason.weight()).append(':')
            .append(escape(reason.message())).append(';'));

        builder.append('|');
        record.commandTypes().forEach(command -> builder
            .append(escape(command)).append(';'));

        return builder.toString();
    }

    public String checksum(List<CombatAiDecisionRecord> records) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (CombatAiDecisionRecord record : records) {
                digest.update(canonical(record).getBytes(StandardCharsets.UTF_8));
                digest.update((byte) '\n');
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\")
            .replace("|", "\\|")
            .replace(";", "\\;");
    }
}
