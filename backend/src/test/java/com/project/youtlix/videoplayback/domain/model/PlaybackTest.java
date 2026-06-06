package com.project.youtlix.videoplayback.domain.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for Playback aggregate from PU14. */
class PlaybackTest {
    @Test
    void playbackCanSaveProgressAndComplete() {
        Playback playback = Playback.create(new ViewerId(UUID.randomUUID()), new ContentId(UUID.randomUUID()));
        playback.start(PlaybackProgress.start());
        playback.updateProgress(new PlaybackProgress(60, Instant.now()));
        assertTrue(playback.isResumable());
        playback.complete();
        assertEquals(PlaybackStatus.COMPLETED, playback.status());
    }
}
