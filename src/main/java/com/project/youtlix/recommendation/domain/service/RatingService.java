package com.project.youtlix.recommendation.domain.service;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.Rating;
import com.project.youtlix.recommendation.domain.model.RatingId;
import com.project.youtlix.recommendation.domain.model.StarRating;

import java.time.Instant;
import java.util.Optional;

/**
 * Domain service responsible for creating or changing viewer ratings.
 */
public class RatingService {

    /**
     * Rates content by creating a new aggregate or changing existing one.
     */
    public Rating rate(Optional<Rating> existing, ViewerId viewerId, ContentId contentId, StarRating stars) {
        if (existing.isPresent()) {
            Rating rating = existing.get();
            rating.changeTo(stars);
            return rating;
        }
        return new Rating(RatingId.newId(), viewerId, contentId, stars, Instant.now());
    }
}
