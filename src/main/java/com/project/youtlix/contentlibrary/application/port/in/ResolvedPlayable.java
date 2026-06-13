package com.project.youtlix.contentlibrary.application.port.in;

import com.project.youtlix.contentlibrary.domain.model.VideoFile;

import java.util.UUID;

/**
 * Playable item resolved from the content catalog for video playback.
 */
public record ResolvedPlayable(UUID id, PlayableKind kind, VideoFile videoFile) {

    public enum PlayableKind {
        MOVIE,
        EPISODE
    }
}
