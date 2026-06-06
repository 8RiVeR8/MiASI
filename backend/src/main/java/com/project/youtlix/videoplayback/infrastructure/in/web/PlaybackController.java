package com.project.youtlix.videoplayback.infrastructure.in.web;

import com.project.youtlix.videoplayback.application.port.in.PlaybackSession;
import com.project.youtlix.videoplayback.application.port.in.PlaybackUseCase;
import com.project.youtlix.videoplayback.domain.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.UUID;

/** Driving web adapter exposing playback endpoints from PU14. */
@RestController
@RequestMapping("/play")
public class PlaybackController {
    private final PlaybackUseCase playback;
    public PlaybackController(PlaybackUseCase playback) { this.playback = playback; }

    @PostMapping("/{contentId}")
    public PlaybackSession play(@PathVariable UUID contentId, @RequestBody PlayRequest request) {
        return playback.play(new ViewerId(request.viewerId()), new ContentId(contentId));
    }

    @PutMapping("/{playbackId}/progress")
    public ResponseEntity<Void> saveProgress(@PathVariable UUID playbackId, @RequestBody ProgressRequest request) {
        playback.saveProgress(new PlaybackId(playbackId), new PlaybackProgress(request.positionSeconds(), Instant.now()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{playbackId}/complete")
    public ResponseEntity<Void> complete(@PathVariable UUID playbackId) {
        playback.finish(new PlaybackId(playbackId));
        return ResponseEntity.ok().build();
    }

    public record PlayRequest(UUID viewerId) {}
    public record ProgressRequest(int positionSeconds) {}
}
