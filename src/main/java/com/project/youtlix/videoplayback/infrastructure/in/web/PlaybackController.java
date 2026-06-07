package com.project.youtlix.videoplayback.infrastructure.in.web;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.videoplayback.application.port.in.PlaybackUseCase;
import com.project.youtlix.videoplayback.domain.model.PlaybackId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import com.project.youtlix.videoplayback.domain.model.VideoStream;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

/**
 * Driving web adapter for playback use cases.
 */
@RestController
@RequestMapping("/playback")
public class PlaybackController {

    private final PlaybackUseCase useCase;

    /** Creates a playback web adapter around the inbound port. */
    public PlaybackController(PlaybackUseCase useCase) {
        this.useCase = useCase;
    }

    /** Starts or resumes playback for selected content. */
    @PostMapping("/{viewerId}/{contentId}")
    public PlaybackResponse play(@PathVariable UUID viewerId, @PathVariable UUID contentId) {
        VideoStream stream = useCase.play(new ViewerId(viewerId), new ContentId(contentId));
        return new PlaybackResponse(stream.uri(), stream.language());
    }

    /** Saves progress for an existing playback. */
    @PutMapping("/progress")
    public void saveProgress(@RequestBody PlaybackRequest request) {
        useCase.saveProgress(
                new PlaybackId(request.playbackId()),
                new PlaybackProgress(request.positionSeconds(), Instant.now())
        );
    }

    /** Marks playback as completed. */
    @PutMapping("/{playbackId}/finish")
    public void finish(@PathVariable UUID playbackId) {
        useCase.finish(new PlaybackId(playbackId));
    }
}
