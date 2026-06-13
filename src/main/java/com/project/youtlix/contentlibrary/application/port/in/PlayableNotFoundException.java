package com.project.youtlix.contentlibrary.application.port.in;

import java.util.UUID;

/**
 * Raised when no movie or episode exists for the requested playable id.
 */
public class PlayableNotFoundException extends RuntimeException {

    public PlayableNotFoundException(UUID id) {
        super("Playable not found: " + id);
    }
}
