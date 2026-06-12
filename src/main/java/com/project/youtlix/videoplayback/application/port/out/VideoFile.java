package com.project.youtlix.videoplayback.application.port.out;

import java.util.List;

/**
 * Technical video file data accepted by playback from the content library.
 *
 * @param uri video file location
 * @param languages available languages
 */
public record VideoFile(String uri, List<String> languages) {

    /** Creates validated technical video data. */
    public VideoFile {
        if (uri == null || uri.isBlank()) {
            throw new IllegalArgumentException("video uri must not be blank");
        }
        languages = languages == null ? List.of() : List.copyOf(languages);
    }
}
