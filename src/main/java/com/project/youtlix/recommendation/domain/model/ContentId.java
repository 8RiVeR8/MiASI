package com.project.youtlix.recommendation.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier of content referenced by recommendation aggregates.
 *
 * @param value content UUID from the library catalog
 */
public record ContentId(UUID value) {

    /** Creates a validated content id. */
    public ContentId {
        Objects.requireNonNull(value, "content id value must not be null");
    }

    /** Creates a new random content id for tests and domain construction. */
    public static ContentId newId() {
        return new ContentId(UUID.randomUUID());
    }
}
