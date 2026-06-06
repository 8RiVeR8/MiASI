package com.project.youtlix.videoplayback.application.service;

import com.project.youtlix.common.infrastructure.event.InMemoryDomainEventBus;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.videoplayback.application.port.out.ContentCatalogPort;
import com.project.youtlix.videoplayback.domain.model.*;
import com.project.youtlix.videoplayback.domain.service.PlaybackService;
import com.project.youtlix.videoplayback.infrastructure.out.cdn.CdnStreamAdapter;
import com.project.youtlix.videoplayback.infrastructure.out.persistence.InMemoryPlaybackRepository;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/** Application tests for PU14 playback flow. */
class PlaybackUseCaseTest {
    @Test
    void playbackResumesFromSavedProgress() {
        ContentCatalogPort catalog = contentId -> new VideoFile("cdn://movie", List.of("pl"));
        PlaybackApplicationService service = new PlaybackApplicationService(
                new InMemoryPlaybackRepository(), catalog, new CdnStreamAdapter(), new PlaybackService(), new InMemoryDomainEventBus());
        ViewerId viewerId = new ViewerId(UUID.randomUUID());
        ContentId contentId = new ContentId(UUID.randomUUID());

        var firstSession = service.play(viewerId, contentId);
        service.saveProgress(firstSession.playbackId(), new PlaybackProgress(120, Instant.now()));
        var resumedSession = service.play(viewerId, contentId);

        assertEquals("cdn://movie", resumedSession.stream().uri());
        assertEquals(120, resumedSession.startPosition().positionSeconds());
    }
}
