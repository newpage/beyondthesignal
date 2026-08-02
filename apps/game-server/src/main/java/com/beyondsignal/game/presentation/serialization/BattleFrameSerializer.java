package com.beyondsignal.game.presentation.serialization;

import com.beyondsignal.game.presentation.frame.BattleFrameV1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;

public final class BattleFrameSerializer {
    private final ObjectMapper mapper;

    public BattleFrameSerializer() {
        this(new ObjectMapper().findAndRegisterModules());
    }

    public BattleFrameSerializer(ObjectMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    public String toJson(BattleFrameV1 frame) {
        try {
            return mapper.writeValueAsString(frame);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize battle frame", exception);
        }
    }

    public BattleFrameV1 fromJson(String json) {
        try {
            return mapper.readValue(json, BattleFrameV1.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid battle frame JSON", exception);
        }
    }
}
