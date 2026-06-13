package com.project.youtlix.videoplayback.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier of a playback aggregate.
 *
 * @param value UUID value stored in playback schema
 */
public record PlaybackId(UUID value) {

    /** Creates a validated playback id. */
    public PlaybackId {
        Objects.requireNonNull(value, "playback id value must not be null");
    }

    /** Creates a new random playback id. */
    public static PlaybackId newId() {
        return new PlaybackId(UUID.randomUUID());
    }
}
