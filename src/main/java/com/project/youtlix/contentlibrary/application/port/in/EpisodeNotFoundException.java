package com.project.youtlix.contentlibrary.application.port.in;

import java.util.UUID;

/**
 * Raised when a library use case targets a missing episode.
 */
public class EpisodeNotFoundException extends RuntimeException {

    public EpisodeNotFoundException(UUID id) {
        super("episode not found: " + id);
    }
}
