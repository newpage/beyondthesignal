package com.beyondsignal.game.presentation.checksum;

import com.beyondsignal.game.presentation.frame.BattleFrameV1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class BattleFrameChecksumService {
    private final ObjectMapper mapper;

    public BattleFrameChecksumService() {
        this.mapper = new ObjectMapper()
            .findAndRegisterModules()
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    public String checksum(BattleFrameV1 frame) {
        try {
            BattleFrameV1 canonical = frame.withMetadata(
                frame.metadata().withChecksum("")
            );
            byte[] bytes = mapper.writeValueAsBytes(canonical);
            return HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256").digest(bytes)
            );
        } catch (JsonProcessingException | NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Unable to calculate frame checksum", exception);
        }
    }
}
