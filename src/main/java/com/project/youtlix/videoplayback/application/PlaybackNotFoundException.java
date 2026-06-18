package com.project.youtlix.videoplayback.application;

import java.util.UUID;

/**
 * Raised when playback progress is updated before playback was started.
 */
public class PlaybackNotFoundException extends RuntimeException {

    public PlaybackNotFoundException(UUID contentId) {
        super("Playback not found for content: " + contentId);
    }
}
