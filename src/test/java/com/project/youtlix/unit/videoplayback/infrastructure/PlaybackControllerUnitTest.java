package com.project.youtlix.unit.videoplayback.infrastructure;

import com.project.youtlix.videoplayback.application.port.in.PlaybackUseCase;
import com.project.youtlix.videoplayback.application.port.in.StartedPlayback;
import com.project.youtlix.videoplayback.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.PlaybackId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import com.project.youtlix.videoplayback.domain.model.ViewerId;
import com.project.youtlix.videoplayback.infrastructure.in.web.PlaybackController;
import com.project.youtlix.videoplayback.infrastructure.in.web.PlaybackRequest;
import com.project.youtlix.videoplayback.infrastructure.in.web.PlaybackResponse;
import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.FixedIdentityProvider;
import com.project.youtlix.testsupport.fixture.ViewerTestAccount;
import com.project.youtlix.videoplayback.domain.model.VideoStream;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UnitTest
class PlaybackControllerUnitTest {

    @Test
    void playSaveProgressAndCompleteUseViewerIdentity() {
        RecordingPlaybackUseCase useCase = new RecordingPlaybackUseCase();
        PlaybackController controller = new PlaybackController(
                useCase,
                new FixedIdentityProvider(ViewerTestAccount.viewerIdentity())
        );
        UUID contentId = UUID.randomUUID();

        PlaybackResponse response = controller.play(ViewerTestAccount.BEARER, contentId);
        controller.saveProgress(ViewerTestAccount.BEARER, contentId, new PlaybackRequest(90));
        controller.finish(ViewerTestAccount.BEARER, contentId);

        assertThat(response.uri()).isEqualTo("cdn://movie");
        assertThat(useCase.viewerId.value()).isEqualTo(ViewerTestAccount.VIEWER_ID);
        assertThat(useCase.savedProgress).isEqualTo(90);
        assertThat(useCase.finished).isTrue();
    }

    @Test
    void playRequiresAuthorizationHeader() {
        PlaybackController controller = new PlaybackController(
                new RecordingPlaybackUseCase(),
                new FixedIdentityProvider(ViewerTestAccount.viewerIdentity())
        );

        assertThatThrownBy(() -> controller.play(" ", UUID.randomUUID()))
                .isInstanceOf(ResponseStatusException.class);
    }

    static class RecordingPlaybackUseCase implements PlaybackUseCase {
        ViewerId viewerId;
        Integer savedProgress;
        boolean finished;

        @Override
        public StartedPlayback play(ViewerId viewerId, ContentId contentId) {
            this.viewerId = viewerId;
            return new StartedPlayback(
                    new VideoStream("cdn://movie", "pl"),
                    PlaybackId.newId(),
                    0,
                    false
            );
        }

        @Override
        public void saveProgress(ViewerId viewerId, ContentId contentId, PlaybackProgress progress) {
            this.viewerId = viewerId;
            this.savedProgress = progress.positionSeconds();
        }

        @Override
        public void finish(ViewerId viewerId, ContentId contentId) {
            this.viewerId = viewerId;
            this.finished = true;
        }
    }
}
