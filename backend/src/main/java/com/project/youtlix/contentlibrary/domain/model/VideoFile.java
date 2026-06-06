package com.project.youtlix.contentlibrary.domain.model;

import java.util.List;

/** Published technical video data owned by the content library. */
public record VideoFile(String uri, List<String> languages) {
    public VideoFile {
        if (uri == null || uri.isBlank()) throw new IllegalArgumentException("Video file uri is required");
        languages = languages == null ? List.of() : List.copyOf(languages);
    }
}
