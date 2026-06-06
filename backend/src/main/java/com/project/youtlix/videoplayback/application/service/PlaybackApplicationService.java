package com.project.youtlix.videoplayback.application.service;

import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import com.project.youtlix.common.domain.DomainException;
import com.project.youtlix.videoplayback.application.port.in.*;
import com.project.youtlix.videoplayback.application.port.out.ContentCatalogPort;
import com.project.youtlix.videoplayback.application.port.out.PlaybackRepository;
import com.project.youtlix.videoplayback.application.port.out.VideoStreamPort;
import com.project.youtlix.videoplayback.domain.model.*;
import com.project.youtlix.videoplayback.domain.service.PlaybackService;
import org.springframework.stereotype.Service;
import java.util.List;

/** Application service implementing playback use case PU14. */
@Service
public class PlaybackApplicationService implements PlaybackUseCase, WatchActivityApi {
    private final PlaybackRepository repository;
    private final ContentCatalogPort catalog;
    private final VideoStreamPort streams;
    private final PlaybackService playbackService;
    private final DomainEventPublisher eventPublisher;

    public PlaybackApplicationService(PlaybackRepository repository, ContentCatalogPort catalog, VideoStreamPort streams,
            PlaybackService playbackService, DomainEventPublisher eventPublisher) {
        this.repository = repository; this.catalog = catalog; this.streams = streams;
        this.playbackService = playbackService; this.eventPublisher = eventPublisher;
    }

    @Override public PlaybackSession play(ViewerId viewerId, ContentId contentId) {
        Playback playback = repository.ofViewerAndContent(viewerId, contentId).orElseGet(() -> Playback.create(viewerId, contentId));
        var file = catalog.videoFileOf(contentId);
        playbackService.play(playback);
        var stream = streams.open(file);
        repository.save(playback);
        eventPublisher.publishAll(playback.occurredEvents());
        return new PlaybackSession(playback.id(), stream, playback.progress());
    }

    @Override public void saveProgress(PlaybackId playbackId, PlaybackProgress progress) {
        Playback playback = repository.ofId(playbackId).orElseThrow(() -> new DomainException("Playback not found"));
        playbackService.saveProgress(playback, progress);
        repository.save(playback);
        eventPublisher.publishAll(playback.occurredEvents());
    }

    @Override public void finish(PlaybackId playbackId) {
        Playback playback = repository.ofId(playbackId).orElseThrow(() -> new DomainException("Playback not found"));
        playbackService.finish(playback);
        repository.save(playback);
        eventPublisher.publishAll(playback.occurredEvents());
    }

    @Override public List<WatchActivity> watchedBy(ViewerId viewerId) {
        return repository.ofViewer(viewerId).stream()
                .map(p -> new WatchActivity(p.viewerId(), p.contentId(), p.progress(), p.status() == PlaybackStatus.COMPLETED))
                .toList();
    }
}
