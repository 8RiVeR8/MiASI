package com.project.youtlix.contentlibrary.domain.model;

import java.util.List;

/**
 * Technical video file data published by the library context.
 *
 * @param uri location of the video file or stream source
 * @param languages available audio/subtitle languages
 */
public record VideoFile(String uri, List<String> languages) {

    /**
     * Creates validated technical video data.
     */
    public VideoFile {
        if (uri == null || uri.isBlank()) {
            throw new IllegalArgumentException("video uri must not be blank");
        }
        languages = languages == null ? List.of() : List.copyOf(languages);
    }
}
