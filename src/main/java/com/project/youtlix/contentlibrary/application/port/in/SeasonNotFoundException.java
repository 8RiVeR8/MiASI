package com.project.youtlix.contentlibrary.application.port.in;

import java.util.UUID;

/**
 * Raised when a library use case targets a missing season.
 */
public class SeasonNotFoundException extends RuntimeException {

    public SeasonNotFoundException(UUID id) {
        super("season not found: " + id);
    }
}
