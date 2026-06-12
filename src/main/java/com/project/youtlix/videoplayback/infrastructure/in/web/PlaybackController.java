package com.project.youtlix.videoplayback.infrastructure.in.web;

import com.project.youtlix.authentication.application.port.out.IdentityProvider;
import com.project.youtlix.videoplayback.application.port.in.PlaybackUseCase;
import com.project.youtlix.videoplayback.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.PlaybackId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import com.project.youtlix.videoplayback.domain.model.ViewerId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

/**
 * Driving web adapter for PU14 playback operations.
 */
@RestController
public class PlaybackController {

    private final PlaybackUseCase useCase;
    private final IdentityProvider identityProvider;

    /** Creates playback web adapter around the inbound port. */
    public PlaybackController(PlaybackUseCase useCase, IdentityProvider identityProvider) {
        this.useCase = useCase;
        this.identityProvider = identityProvider;
    }

    /** Handles POST /play/{contentId}. */
    @PostMapping("/play/{contentId}")
    public PlaybackResponse play(@RequestHeader("Authorization") String authorization, @PathVariable UUID contentId) {
        return PlaybackResponse.from(useCase.play(currentViewer(authorization), new ContentId(contentId)));
    }

    /** Handles PUT /play/{contentId}/progress. */
    @PutMapping("/play/{contentId}/progress")
    public void saveProgress(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID contentId,
            @RequestBody PlaybackRequest request
    ) {
        currentViewer(authorization);
        useCase.saveProgress(
                new PlaybackId(request.playbackId()),
                new PlaybackProgress(request.positionSeconds(), Instant.now())
        );
    }

    /** Handles POST /play/{contentId}/complete. */
    @PostMapping("/play/{contentId}/complete")
    public void finish(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID contentId,
            @RequestBody PlaybackRequest request
    ) {
        currentViewer(authorization);
        useCase.finish(new PlaybackId(request.playbackId()));
    }

    private ViewerId currentViewer(String authorization) {
        return new ViewerId(identityProvider.currentIdentity(bearerToken(authorization)).viewerId().value());
    }

    private String bearerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is required");
        }
        return authorization.startsWith("Bearer ") ? authorization.substring("Bearer ".length()) : authorization;
    }
}
