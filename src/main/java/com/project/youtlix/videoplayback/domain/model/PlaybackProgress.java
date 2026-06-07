package com.project.youtlix.videoplayback.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Current playback position and update timestamp.
 *
 * @param positionSeconds position in seconds
 * @param updatedAt update time
 */
public record PlaybackProgress(int positionSeconds, Instant updatedAt) {

    /** Creates a validated playback progress value. */
    public PlaybackProgress {
        if (positionSeconds < 0) {
            throw new IllegalArgumentException("positionSeconds must not be negative");
        }
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    /** Creates progress at the beginning of a video. */
    public static PlaybackProgress start() {
        return new PlaybackProgress(0, Instant.now());
    }

    /** Checks whether this progress points at the beginning. */
    public boolean isStart() {
        return positionSeconds == 0;
    }
}
