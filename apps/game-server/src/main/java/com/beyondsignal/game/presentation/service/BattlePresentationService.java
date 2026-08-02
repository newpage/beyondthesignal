package com.beyondsignal.game.presentation.service;

import com.beyondsignal.game.presentation.checksum.BattleFrameChecksumService;
import com.beyondsignal.game.presentation.frame.BattleFrameV1;
import com.beyondsignal.game.presentation.recorder.FrameRecorder;
import java.util.List;
import java.util.Objects;

public final class BattlePresentationService {
    private final BattleFrameChecksumService checksumService;
    private final List<FrameRecorder> recorders;

    public BattlePresentationService(
        BattleFrameChecksumService checksumService,
        List<FrameRecorder> recorders
    ) {
        this.checksumService = Objects.requireNonNull(checksumService, "checksumService");
        this.recorders = List.copyOf(recorders);
    }

    public BattleFrameV1 publish(BattleFrameV1 unsignedFrame) {
        String checksum = checksumService.checksum(unsignedFrame);
        BattleFrameV1 signed = unsignedFrame.withMetadata(
            unsignedFrame.metadata().withChecksum(checksum)
        );
        recorders.forEach(recorder -> recorder.record(signed));
        return signed;
    }
}
