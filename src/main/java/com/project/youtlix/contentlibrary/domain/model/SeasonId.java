package com.project.youtlix.contentlibrary.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier of a season entity inside a series aggregate.
 *
 * @param value UUID value stored in the library schema
 */
public record SeasonId(UUID value) {

    /**
     * Creates a validated season id.
     */
    public SeasonId {
        Objects.requireNonNull(value, "season id value must not be null");
    }

    /**
     * Creates a new random season id.
     *
     * @return new season id
     */
    public static SeasonId newId() {
        return new SeasonId(UUID.randomUUID());
    }
}
