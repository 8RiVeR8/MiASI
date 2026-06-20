package com.project.youtlix.contentlibrary.infrastructure.in.web;

import java.util.List;

/**
 * Request body for adding or updating an episode.
 */
public record EpisodeRequest(
        int number,
        String title,
        Integer durationSeconds,
        String videoUri,
        List<String> languages
) {
}
