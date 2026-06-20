package com.project.youtlix.unit.videoplayback.application;

import com.project.youtlix.videoplayback.application.service.PlaybackApplicationService;
import com.project.youtlix.videoplayback.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import com.project.youtlix.videoplayback.domain.model.PlaybackStatus;
import com.project.youtlix.videoplayback.domain.service.PlaybackService;
import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.NoOpDomainEventPublisher;
import com.project.youtlix.testsupport.fixture.ViewerTestAccount;
import com.project.youtlix.testsupport.fixture.memory.InMemoryPlaybackRepository;
import com.project.youtlix.testsupport.fixture.stub.FakePlaybackContentCatalogApi;
import com.project.youtlix.testsupport.fixture.stub.FakeVideoStreamPort;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class ViewerPlaybackLifecycleUnitTest {

    @Test
    void viewerPlaybackLifecycle_startProgressComplete() {
        InMemoryPlaybackRepository repository = new InMemoryPlaybackRepository();
        PlaybackApplicationService service = new PlaybackApplicationService(
                repository,
                new FakePlaybackContentCatalogApi(),
                new FakeVideoStreamPort(),
                new NoOpDomainEventPublisher(),
                new PlaybackService()
        );
        var viewerId = new com.project.youtlix.videoplayback.domain.model.ViewerId(ViewerTestAccount.VIEWER_ID);
        ContentId contentId = new ContentId(UUID.randomUUID());

        service.play(viewerId, contentId);
        service.saveProgress(viewerId, contentId, new PlaybackProgress(300, Instant.now()));
        service.finish(viewerId, contentId);

        assertThat(repository.onlyPlayback().status()).isEqualTo(PlaybackStatus.COMPLETED);
        assertThat(repository.onlyPlayback().progress().positionSeconds()).isEqualTo(300);
    }
}
