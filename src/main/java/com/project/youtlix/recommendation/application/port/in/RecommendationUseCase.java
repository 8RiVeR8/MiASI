package com.project.youtlix.recommendation.application.port.in;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.StarRating;

/**
 * Inbound port for PU11-PU13 recommendation use cases.
 */
public interface RecommendationUseCase {

    /** Generates recommendations for a viewer. */
    RecommendationList generateFor(ViewerId viewerId);

    /** Rates content with one to five stars. */
    void rate(ViewerId viewerId, ContentId contentId, StarRating stars);

    /** Adds content to viewer watchlist. */
    void addToWatchlist(ViewerId viewerId, ContentId contentId);

    /** Removes content from viewer watchlist. */
    void removeFromWatchlist(ViewerId viewerId, ContentId contentId);
}
