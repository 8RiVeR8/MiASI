package com.project.youtlix.recommendation.domain.service;

import com.project.youtlix.recommendation.domain.model.*;

/** Domain service responsible for PU12 rating decisions. */
public class RatingService {
    /** Creates or updates a rating. */
    public Rating rate(Rating existing, ViewerId viewerId, ContentId contentId, StarRating stars) {
        if (existing == null) return Rating.create(viewerId, contentId, stars);
        existing.changeTo(stars);
        return existing;
    }
}
