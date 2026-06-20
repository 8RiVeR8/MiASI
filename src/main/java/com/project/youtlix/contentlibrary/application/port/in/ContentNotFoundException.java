package com.project.youtlix.contentlibrary.application.port.in;

import java.util.UUID;

/**
 * Raised when a library use case targets missing content.
 */
public class ContentNotFoundException extends RuntimeException {

    public ContentNotFoundException(UUID id) {
        super("content not found: " + id);
    }
}
