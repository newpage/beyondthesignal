package com.beyondsignal.game.persistence;

import com.beyondsignal.game.exploration.ExplorationCampaignState;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

public final class FileExplorationStateRepository {
    private final Path file;
    private final ExplorationStateCodec codec;

    public FileExplorationStateRepository(Path file) {
        this(file, new ExplorationStateCodec());
    }

    public FileExplorationStateRepository(Path file, ExplorationStateCodec codec) {
        this.file = Objects.requireNonNull(file, "file");
        this.codec = Objects.requireNonNull(codec, "codec");
    }

    public synchronized void save(ExplorationCampaignState state) {
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
            Files.writeString(temporary, codec.encode(state));
            Files.move(
                temporary,
                file,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save exploration state", exception);
        }
    }

    public synchronized Optional<ExplorationCampaignState> load() {
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        try {
            return Optional.of(codec.decode(Files.readString(file)));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load exploration state", exception);
        }
    }
}
