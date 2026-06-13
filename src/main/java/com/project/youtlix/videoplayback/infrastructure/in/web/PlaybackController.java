package com.project.youtlix.videoplayback.infrastructure.in.web;

import com.project.youtlix.authentication.application.port.out.IdentityProvider;
import com.project.youtlix.contentlibrary.application.port.in.PlayableNotFoundException;
import com.project.youtlix.contentlibrary.application.port.in.SeriesNotPlayableException;
import com.project.youtlix.common.infrastructure.in.web.OpenApiConfig;
import com.project.youtlix.videoplayback.application.PlaybackNotFoundException;
import com.project.youtlix.videoplayback.application.port.in.PlaybackUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.project.youtlix.videoplayback.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import com.project.youtlix.videoplayback.domain.model.ViewerId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Driving web adapter for PU14 playback operations.
 */
@RestController
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
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
        ViewerId viewerId = currentViewer(authorization);
        return PlaybackResponse.from(useCase.play(viewerId, new ContentId(contentId)));
    }

    /** Handles PUT /play/{contentId}/progress. */
    @PutMapping("/play/{contentId}/progress")
    public void saveProgress(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID contentId,
            @RequestBody PlaybackRequest request
    ) {
        ViewerId viewerId = currentViewer(authorization);
        useCase.saveProgress(
                viewerId,
                new ContentId(contentId),
                new PlaybackProgress(request.positionSeconds(), Instant.now())
        );
    }

    /** Handles POST /play/{contentId}/complete. */
    @PostMapping("/play/{contentId}/complete")
    public void finish(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID contentId
    ) {
        useCase.finish(currentViewer(authorization), new ContentId(contentId));
    }

    @ExceptionHandler(SeriesNotPlayableException.class)
    public ResponseEntity<Map<String, String>> handleSeriesNotPlayable(SeriesNotPlayableException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(PlayableNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePlayableNotFound(PlayableNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(PlaybackNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePlaybackNotFound(PlaybackNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage()));
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
