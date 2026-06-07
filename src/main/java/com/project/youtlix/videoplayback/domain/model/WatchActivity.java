package com.project.youtlix.videoplayback.domain.model;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.contentlibrary.domain.model.ContentId;

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
