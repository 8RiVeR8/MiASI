package com.project.youtlix.videoplayback.application.service;

import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import com.project.youtlix.contentlibrary.application.port.in.ContentCatalogApi;
import com.project.youtlix.videoplayback.application.port.in.PlaybackUseCase;
import com.project.youtlix.videoplayback.application.port.in.WatchActivityApi;
import com.project.youtlix.videoplayback.application.port.out.PlaybackRepository;
import com.project.youtlix.videoplayback.application.port.out.VideoFile;
import com.project.youtlix.videoplayback.application.port.out.VideoStreamPort;
import com.project.youtlix.videoplayback.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.Playback;
import com.project.youtlix.videoplayback.domain.model.PlaybackId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import com.project.youtlix.videoplayback.domain.model.PlaybackStatus;
import com.project.youtlix.videoplayback.domain.model.ViewerId;
import com.project.youtlix.videoplayback.application.port.in.StartedPlayback;
import com.project.youtlix.videoplayback.domain.model.VideoStream;
import com.project.youtlix.videoplayback.domain.model.WatchActivity;
import com.project.youtlix.videoplayback.domain.service.PlaybackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service coordinating playback use cases with catalog, CDN and persistence ports.
 */
@Service
public class PlaybackApplicationService implements PlaybackUseCase, WatchActivityApi {

    private final PlaybackRepository playbackRepository;
    private final ContentCatalogApi contentCatalogApi;
    private final VideoStreamPort videoStreamPort;
    private final DomainEventPublisher eventPublisher;
    private final PlaybackService playbackService;

    /**
     * Creates playback application service with the default domain service.
     */
    @Autowired
    public PlaybackApplicationService(
            PlaybackRepository playbackRepository,
            ContentCatalogApi contentCatalogApi,
            VideoStreamPort videoStreamPort,
            DomainEventPublisher eventPublisher
    ) {
        this(playbackRepository, contentCatalogApi, videoStreamPort, eventPublisher, new PlaybackService());
    }

    /** Constructor useful for tests with an explicit domain service. */
    public PlaybackApplicationService(
            PlaybackRepository playbackRepository,
            ContentCatalogApi contentCatalogApi,
            VideoStreamPort videoStreamPort,
            DomainEventPublisher eventPublisher,
            PlaybackService playbackService
    ) {
        this.playbackRepository = playbackRepository;
        this.contentCatalogApi = contentCatalogApi;
        this.videoStreamPort = videoStreamPort;
        this.eventPublisher = eventPublisher;
        this.playbackService = playbackService;
    }

    @Override
    public StartedPlayback play(ViewerId viewerId, ContentId contentId) {
        Playback playback = playbackRepository.ofViewerAndContent(viewerId, contentId)
                .orElseGet(() -> new Playback(PlaybackId.newId(), viewerId, contentId));
        com.project.youtlix.contentlibrary.domain.model.VideoFile catalogVideoFile =
                contentCatalogApi.videoFileOf(new com.project.youtlix.contentlibrary.domain.model.ContentId(contentId.value()));

        boolean resumed = playback.isResumable();
        int resumeFromSeconds = resumed ? playback.progress().positionSeconds() : 0;
        playbackService.play(playback);
        VideoStream stream = videoStreamPort.open(new VideoFile(catalogVideoFile.uri(), catalogVideoFile.languages()));
        playbackRepository.save(playback);
        eventPublisher.publishAll(playback.occurredEvents());
        return new StartedPlayback(stream, playback.id(), resumeFromSeconds, resumed);
    }

    @Override
    public void saveProgress(ViewerId viewerId, ContentId contentId, PlaybackProgress progress) {
        Playback playback = playbackRepository.ofViewerAndContent(viewerId, contentId)
                .orElseThrow(() -> new IllegalArgumentException("playback not found for content: " + contentId.value()));
        playbackService.saveProgress(playback, progress);
        playbackRepository.save(playback);
        eventPublisher.publishAll(playback.occurredEvents());
    }

    @Override
    public void finish(ViewerId viewerId, ContentId contentId) {
        Playback playback = playbackRepository.ofViewerAndContent(viewerId, contentId)
                .orElseThrow(() -> new IllegalArgumentException("playback not found for content: " + contentId.value()));
        playbackService.finish(playback);
        playbackRepository.save(playback);
        eventPublisher.publishAll(playback.occurredEvents());
    }

    @Override
    public List<WatchActivity> watchedBy(ViewerId viewerId) {
        return playbackRepository.ofViewer(viewerId).stream()
                .map(playback -> new WatchActivity(
                        playback.viewerId(),
                        playback.contentId(),
                        playback.progress(),
                        playback.status() == PlaybackStatus.COMPLETED
                ))
                .toList();
    }
}
