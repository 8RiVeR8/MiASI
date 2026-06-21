package com.project.youtlix.unit.videoplayback.domain;

import com.project.youtlix.videoplayback.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.PlayableType;
import com.project.youtlix.videoplayback.domain.model.Playback;
import com.project.youtlix.videoplayback.domain.model.PlaybackId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import com.project.youtlix.videoplayback.domain.model.PlaybackStatus;
import com.project.youtlix.videoplayback.domain.model.ViewerId;
import com.project.youtlix.videoplayback.domain.service.PlaybackService;
import com.project.youtlix.testsupport.annotation.UnitTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class PlaybackServiceUnitTest {

    private final PlaybackService playbackService = new PlaybackService();

    @Test
    void playResumesFromSavedProgressWhenPossible() {
        Playback playback = new Playback(
                PlaybackId.newId(),
                new ViewerId(UUID.randomUUID()),
                new ContentId(UUID.randomUUID()),
                PlayableType.MOVIE,
                new PlaybackProgress(300, Instant.now()),
                PlaybackStatus.PAUSED
        );

        playbackService.play(playback);

        assertThat(playback.status()).isEqualTo(PlaybackStatus.PLAYING);
        assertThat(playback.progress().positionSeconds()).isEqualTo(300);
    }

    @Test
    void playStartsFromBeginningWhenNotResumable() {
        Playback playback = new Playback(
                PlaybackId.newId(),
                new ViewerId(UUID.randomUUID()),
                new ContentId(UUID.randomUUID()),
                PlayableType.MOVIE
        );
        playback.complete();

        playbackService.play(playback);

        assertThat(playback.progress().positionSeconds()).isZero();
    }
}
