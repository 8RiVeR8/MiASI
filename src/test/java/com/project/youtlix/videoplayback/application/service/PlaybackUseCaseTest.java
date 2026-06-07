package com.project.youtlix.videoplayback.application.service;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import com.project.youtlix.common.domain.model.DomainEvent;
import com.project.youtlix.contentlibrary.application.port.in.ContentCatalogApi;
import com.project.youtlix.contentlibrary.application.port.in.ContentMetadata;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.videoplayback.application.port.out.PlaybackRepository;
import com.project.youtlix.videoplayback.application.port.out.VideoStreamPort;
import com.project.youtlix.videoplayback.domain.model.Playback;
import com.project.youtlix.videoplayback.domain.model.PlaybackId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import com.project.youtlix.videoplayback.domain.model.PlaybackStatus;
import com.project.youtlix.videoplayback.domain.model.VideoStream;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PlaybackUseCaseTest {

    @Test
    void playbackResumesFromSavedProgress() {
        InMemoryPlaybackRepository repository = new InMemoryPlaybackRepository();
        PlaybackApplicationService service = new PlaybackApplicationService(
                repository,
                new FakeContentCatalogApi(),
                new FakeVideoStreamPort(),
                new RecordingPublisher()
        );
        ViewerId viewerId = new ViewerId(UUID.randomUUID());
        ContentId contentId = ContentId.newId();

        service.play(viewerId, contentId);
        PlaybackId playbackId = repository.onlyPlayback().id();
        service.saveProgress(playbackId, new PlaybackProgress(120, Instant.now()));
        service.play(viewerId, contentId);

        assertThat(repository.onlyPlayback().progress().positionSeconds()).isEqualTo(120);
        assertThat(repository.onlyPlayback().status()).isEqualTo(PlaybackStatus.PLAYING);
    }

    static class InMemoryPlaybackRepository implements PlaybackRepository {
        private final Map<PlaybackId, Playback> playbacks = new LinkedHashMap<>();

        @Override
        public void save(Playback playback) {
            playbacks.put(playback.id(), playback);
        }

        @Override
        public Optional<Playback> ofId(PlaybackId id) {
            return Optional.ofNullable(playbacks.get(id));
        }

        @Override
        public Optional<Playback> ofViewerAndContent(ViewerId viewerId, ContentId contentId) {
            return playbacks.values().stream()
                    .filter(playback -> playback.viewerId().equals(viewerId) && playback.contentId().equals(contentId))
                    .findFirst();
        }

        @Override
        public List<Playback> ofViewer(ViewerId viewerId) {
            return playbacks.values().stream()
                    .filter(playback -> playback.viewerId().equals(viewerId))
                    .toList();
        }

        Playback onlyPlayback() {
            return playbacks.values().iterator().next();
        }
    }

    static class FakeContentCatalogApi implements ContentCatalogApi {
        @Override
        public List<ContentId> popularContent(int limit) {
            return List.of();
        }

        @Override
        public ContentMetadata metadataOf(ContentId id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public VideoFile videoFileOf(ContentId id) {
            return new VideoFile("cdn://movie", List.of("pl"));
        }
    }

    static class FakeVideoStreamPort implements VideoStreamPort {
        @Override
        public VideoStream open(VideoFile file) {
            return new VideoStream(file.uri(), file.languages().getFirst());
        }
    }

    static class RecordingPublisher implements DomainEventPublisher {
        final List<DomainEvent> events = new ArrayList<>();

        @Override
        public void publish(DomainEvent event) {
            events.add(event);
        }
    }
}
