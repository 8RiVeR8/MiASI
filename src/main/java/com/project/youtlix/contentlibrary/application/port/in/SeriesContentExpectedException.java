package com.project.youtlix.contentlibrary.application.port.in;

import java.util.UUID;

/**
 * Raised when a series-only operation targets non-series content.
 */
public class SeriesContentExpectedException extends RuntimeException {

    public SeriesContentExpectedException(UUID id) {
        super("content is not a series: " + id);
    }
}
