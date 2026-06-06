package com.project.youtlix.recommendation.application.port.in;

import com.project.youtlix.recommendation.domain.model.*;

/** Inbound port exposing PU11-PU13 recommendation use cases. */
public interface RecommendationUseCase {
    /** Generates recommendations for a viewer. */
    RecommendationList generateFor(ViewerId viewerId);
    /** Rates content for a viewer. */
    void rate(ViewerId viewerId, ContentId contentId, StarRating stars);
    /** Adds content to a viewer watchlist. */
    void addToWatchlist(ViewerId viewerId, ContentId contentId);
    /** Removes content from a viewer watchlist. */
    void removeFromWatchlist(ViewerId viewerId, ContentId contentId);
}
