package com.project.youtlix.recommendation.domain.model;

/**
 * Star rating in the 1-5 range.
 *
 * @param value rating value
 */
public record StarRating(int value) {

    /** Creates a validated star rating. */
    public StarRating {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("star rating must be between 1 and 5");
        }
    }

    /** Checks whether rating is positive for recommendation purposes. */
    public boolean isPositive() {
        return value >= 4;
    }
}
