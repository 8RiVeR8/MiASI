package com.project.youtlix.contentlibrary.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier of a content aggregate.
 *
 * @param value UUID value stored in the library schema
 */
public record ContentId(UUID value) {

    /**
     * Creates a validated content id.
     */
    public ContentId {
        Objects.requireNonNull(value, "content id value must not be null");
    }

    /**
     * Creates a new random content id.
     *
     * @return new content id
     */
    public static ContentId newId() {
        return new ContentId(UUID.randomUUID());
    }
}
