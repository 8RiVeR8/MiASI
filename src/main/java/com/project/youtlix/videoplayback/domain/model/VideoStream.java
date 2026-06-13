package com.project.youtlix.videoplayback.domain.model;

/**
 * Opened stream returned to the caller by playback use case.
 *
 * @param uri stream uri
 * @param language selected language
 */
public record VideoStream(String uri, String language) {

    /** Creates a validated video stream descriptor. */
    public VideoStream {
        if (uri == null || uri.isBlank()) {
            throw new IllegalArgumentException("stream uri must not be blank");
        }
        if (language == null || language.isBlank()) {
            language = "default";
        }
    }
}
