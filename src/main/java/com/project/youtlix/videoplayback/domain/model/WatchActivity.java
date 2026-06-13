package com.project.youtlix.videoplayback.domain.model;

/**
 * Published playback activity consumed by the recommendation context.
 *
 * @param viewerId viewer who watched content
 * @param contentId watched content id
 * @param progress last saved progress
 * @param completed whether playback was completed
 */
public record WatchActivity(
        ViewerId viewerId,
        ContentId contentId,
        PlaybackProgress progress,
        boolean completed
) {
}
