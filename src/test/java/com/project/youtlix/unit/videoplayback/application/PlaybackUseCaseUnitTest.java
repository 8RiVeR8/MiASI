package com.project.youtlix.unit.videoplayback.application;

import com.project.youtlix.videoplayback.application.service.PlaybackApplicationService;
import com.project.youtlix.videoplayback.application.port.in.StartedPlayback;
import com.project.youtlix.videoplayback.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import com.project.youtlix.videoplayback.domain.model.PlaybackStatus;
import com.project.youtlix.videoplayback.domain.model.ViewerId;
import com.project.youtlix.videoplayback.domain.service.PlaybackService;
import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.RecordingDomainEventPublisher;
import com.project.youtlix.testsupport.fixture.memory.InMemoryPlaybackRepository;
import com.project.youtlix.testsupport.fixture.stub.FakePlaybackContentCatalogApi;
import com.project.youtlix.testsupport.fixture.stub.FakeVideoStreamPort;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class PlaybackUseCaseUnitTest {

    @Test
    void playbackResumesFromSavedProgress() {
        InMemoryPlaybackRepository repository = new InMemoryPlaybackRepository();
        PlaybackApplicationService service = new PlaybackApplicationService(
                repository,
                new FakePlaybackContentCatalogApi(),
                new FakeVideoStreamPort(),
                new RecordingDomainEventPublisher(),
                new PlaybackService()
        );
        ViewerId viewerId = new ViewerId(UUID.randomUUID());
        ContentId contentId = new ContentId(UUID.randomUUID());

        service.play(viewerId, contentId);
        service.saveProgress(viewerId, contentId, new PlaybackProgress(120, Instant.now()));
        StartedPlayback resumed = service.play(viewerId, contentId);

        assertThat(repository.onlyPlayback().progress().positionSeconds()).isEqualTo(120);
        assertThat(repository.onlyPlayback().status()).isEqualTo(PlaybackStatus.PLAYING);
        assertThat(resumed.resumed()).isTrue();
        assertThat(resumed.resumeFromSeconds()).isEqualTo(120);
    }
}
