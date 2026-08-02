package com.beyondsignal.game.combat.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;

public final class CombatEventCodec {
    private final ObjectMapper mapper;

    public CombatEventCodec() {
        this(new ObjectMapper().findAndRegisterModules());
    }

    public CombatEventCodec(ObjectMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    public String encode(CombatEventBatch batch) {
        try {
            return mapper.writeValueAsString(batch);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to encode combat event batch", exception);
        }
    }

    public CombatEventBatch decode(String json) {
        try {
            return mapper.readValue(json, CombatEventBatch.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid combat event batch JSON", exception);
        }
    }
}
