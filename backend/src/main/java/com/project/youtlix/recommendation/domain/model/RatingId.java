package com.project.youtlix.recommendation.domain.model;

import java.util.UUID;

/** Value object identifying a rating aggregate. */
public record RatingId(UUID value) {
    public RatingId { if (value == null) throw new IllegalArgumentException("Rating id is required"); }
    /** Creates a new rating identifier. */
    public static RatingId newId() { return new RatingId(UUID.randomUUID()); }
}
