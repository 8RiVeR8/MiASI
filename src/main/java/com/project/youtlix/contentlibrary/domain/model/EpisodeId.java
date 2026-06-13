package com.project.youtlix.contentlibrary.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier of an episode entity inside a series aggregate.
 *
 * @param value UUID value stored in the library schema
 */
public record EpisodeId(UUID value) {

    /**
     * Creates a validated episode id.
     */
    public EpisodeId {
        Objects.requireNonNull(value, "episode id value must not be null");
    }

    /**
     * Creates a new random episode id.
     *
     * @return new episode id
     */
    public static EpisodeId newId() {
        return new EpisodeId(UUID.randomUUID());
    }
}
