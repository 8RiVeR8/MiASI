package com.project.youtlix.videoplayback.domain.model;

import java.util.UUID;

/** Value object identifying playback history. */
public record PlaybackId(UUID value) {
    public PlaybackId { if (value == null) throw new IllegalArgumentException("Playback id is required"); }
    /** Creates a new playback identifier. */
    public static PlaybackId newId() { return new PlaybackId(UUID.randomUUID()); }
}
