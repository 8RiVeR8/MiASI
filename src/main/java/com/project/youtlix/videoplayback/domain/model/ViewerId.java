package com.project.youtlix.videoplayback.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier of a viewer referenced by playback.
 *
 * @param value Supabase Auth user UUID
 */
public record ViewerId(UUID value) {

    /** Creates a validated viewer id. */
    public ViewerId {
        Objects.requireNonNull(value, "viewer id value must not be null");
    }
}
