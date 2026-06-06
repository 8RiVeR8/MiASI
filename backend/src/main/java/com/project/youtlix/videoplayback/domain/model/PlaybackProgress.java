package com.project.youtlix.videoplayback.domain.model;

import java.time.Instant;

/** Value object representing playback position and update time. */
public record PlaybackProgress(int positionSeconds, Instant updatedAt) {
    public PlaybackProgress {
        if (positionSeconds < 0) throw new IllegalArgumentException("Position cannot be negative");
        updatedAt = updatedAt == null ? Instant.now() : updatedAt;
    }
    /** Creates progress at the beginning of the content. */
    public static PlaybackProgress start() { return new PlaybackProgress(0, Instant.now()); }
    /** Returns true when playback starts from the beginning. */
    public boolean isStart() { return positionSeconds == 0; }
}
