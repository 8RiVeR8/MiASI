package com.project.youtlix.videoplayback.domain.model;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.event.PlaybackFinished;
import com.project.youtlix.videoplayback.domain.model.event.PlaybackProgressSaved;
import com.project.youtlix.videoplayback.domain.model.event.PlaybackStarted;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PlaybackTest {

    @Test
    void playbackCanSaveProgressAndComplete() {
        Playback playback = new Playback(
                PlaybackId.newId(),
                new ViewerId(UUID.randomUUID()),
                ContentId.newId()
        );

        playback.start(PlaybackProgress.start());
        playback.updateProgress(new PlaybackProgress(120, Instant.now()));
        playback.complete();

        assertThat(playback.status()).isEqualTo(PlaybackStatus.COMPLETED);
        assertThat(playback.progress().positionSeconds()).isEqualTo(120);
        assertThat(playback.occurredEvents())
                .hasAtLeastOneElementOfType(PlaybackStarted.class)
                .hasAtLeastOneElementOfType(PlaybackProgressSaved.class)
                .hasAtLeastOneElementOfType(PlaybackFinished.class);
    }
}
