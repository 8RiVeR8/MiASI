package com.project.youtlix.contentlibrary.infrastructure.in.web;

import java.util.List;

/**
 * Request body for adding an episode to a series season.
 */
public record EpisodeRequest(
        int number,
        String title,
        Integer durationSeconds,
        String videoUri,
        List<String> languages
) {
}
