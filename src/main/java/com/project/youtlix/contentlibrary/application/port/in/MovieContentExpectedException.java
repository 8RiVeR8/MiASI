package com.project.youtlix.contentlibrary.application.port.in;

import java.util.UUID;

/**
 * Raised when an operation requiring movie content receives a different type.
 */
public class MovieContentExpectedException extends RuntimeException {

    public MovieContentExpectedException(UUID contentId) {
        super("content is not a movie: " + contentId);
    }
}
