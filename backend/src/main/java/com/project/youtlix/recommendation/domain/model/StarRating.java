package com.project.youtlix.recommendation.domain.model;

/** Value object representing a 1-5 star rating. */
public record StarRating(int value) {
    public StarRating { if (value < 1 || value > 5) throw new IllegalArgumentException("Rating must be between 1 and 5"); }
    /** Returns true for ratings treated as positive input to recommendations. */
    public boolean isPositive() { return value >= 4; }
}
