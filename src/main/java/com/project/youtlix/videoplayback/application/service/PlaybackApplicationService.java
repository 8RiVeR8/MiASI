package com.project.youtlix.videoplayback.application.service;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import com.project.youtlix.contentlibrary.application.port.in.ContentCatalogApi;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.videoplayback.application.port.in.PlaybackUseCase;
import com.project.youtlix.videoplayback.application.port.in.WatchActivityApi;
import com.project.youtlix.videoplayback.application.port.out.PlaybackRepository;
import com.project.youtlix.videoplayback.application.port.out.VideoStreamPort;
import com.project.youtlix.videoplayback.domain.model.Playback;
import com.project.youtlix.videoplayback.domain.model.PlaybackId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import com.project.youtlix.videoplayback.domain.model.PlaybackStatus;
import com.project.youtlix.videoplayback.domain.model.VideoStream;
import com.project.youtlix.videoplayback.domain.model.WatchActivity;
import com.project.youtlix.videoplayback.domain.service.PlaybackService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service coordinating playback use case with catalog and stream ports.
 */
@Service
public class PlaybackApplicationService implements PlaybackUseCase, WatchActivityApi {

    private final PlaybackRepository playbackRepository;
    private final ContentCatalogApi contentCatalogApi;
    private final VideoStreamPort videoStreamPort;
    private final DomainEventPublisher eventPublisher;
    private final PlaybackService playbackService;

    /**
     * Creates playback application service.
     */
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
    public VideoStream play(ViewerId viewerId, ContentId contentId) {
        Playback playback = playbackRepository.ofViewerAndContent(viewerId, contentId)
                .orElseGet(() -> new Playback(PlaybackId.newId(), viewerId, contentId));
        playbackService.play(playback);
        playbackRepository.save(playback);
        eventPublisher.publishAll(playback.occurredEvents());

        VideoFile videoFile = contentCatalogApi.videoFileOf(contentId);
        return videoStreamPort.open(videoFile);
    }

    @Override
    public void saveProgress(PlaybackId playbackId, PlaybackProgress progress) {
        Playback playback = playbackRepository.ofId(playbackId)
                .orElseThrow(() -> new IllegalArgumentException("playback not found: " + playbackId.value()));
        playback.updateProgress(progress);
        playbackRepository.save(playback);
        eventPublisher.publishAll(playback.occurredEvents());
    }

    @Override
    public void finish(PlaybackId playbackId) {
        Playback playback = playbackRepository.ofId(playbackId)
                .orElseThrow(() -> new IllegalArgumentException("playback not found: " + playbackId.value()));
        playback.complete();
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
