package com.project.youtlix.unit.videoplayback.domain;

import com.project.youtlix.videoplayback.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.PlayableType;
import com.project.youtlix.videoplayback.domain.model.Playback;
import com.project.youtlix.videoplayback.domain.model.PlaybackId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import com.project.youtlix.videoplayback.domain.model.PlaybackStatus;
import com.project.youtlix.videoplayback.domain.model.ViewerId;
import com.project.youtlix.videoplayback.domain.model.event.PlaybackFinished;
import com.project.youtlix.videoplayback.domain.model.event.PlaybackProgressSaved;
import com.project.youtlix.videoplayback.domain.model.event.PlaybackStarted;
import com.project.youtlix.testsupport.annotation.UnitTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class PlaybackDomainUnitTest {

    @Test
    void playbackCanSaveProgressAndComplete() {
        Playback playback = new Playback(
                PlaybackId.newId(),
                new ViewerId(UUID.randomUUID()),
                new ContentId(UUID.randomUUID()),
                PlayableType.MOVIE
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
