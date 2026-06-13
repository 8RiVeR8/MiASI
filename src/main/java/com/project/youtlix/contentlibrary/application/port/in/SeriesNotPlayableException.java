package com.project.youtlix.contentlibrary.application.port.in;

import java.util.UUID;

/**
 * Raised when playback is requested for a series container instead of an episode.
 */
public class SeriesNotPlayableException extends RuntimeException {

    public SeriesNotPlayableException(UUID id) {
        super("Series cannot be played directly; use an episode id instead of content id: " + id);
    }
}
