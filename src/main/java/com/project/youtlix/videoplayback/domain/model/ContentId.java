package com.project.youtlix.videoplayback.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier of content referenced by playback.
 *
 * @param value content UUID from the library catalog
 */
public record ContentId(UUID value) {

    /** Creates a validated content id. */
    public ContentId {
        Objects.requireNonNull(value, "content id value must not be null");
    }
}
