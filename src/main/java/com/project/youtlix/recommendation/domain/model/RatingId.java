package com.project.youtlix.recommendation.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier of a rating aggregate.
 *
 * @param value UUID value stored in recommendation schema
 */
public record RatingId(UUID value) {

    /** Creates a validated rating id. */
    public RatingId {
        Objects.requireNonNull(value, "rating id value must not be null");
    }

    /** Creates a new random rating id. */
    public static RatingId newId() {
        return new RatingId(UUID.randomUUID());
    }
}
