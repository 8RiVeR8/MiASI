package com.project.youtlix.recommendation.application.port.in;

import com.project.youtlix.recommendation.domain.model.*;

import java.util.List;

/**
 * Inbound port for PU11-PU13 recommendation use cases.
 */
public interface RecommendationUseCase {

    /** Generates recommendations for a viewer. */
    RecommendationList generateFor(ViewerId viewerId);

    List<RecommendationResponse> toContentResponses(RecommendationList recommendations);

    /** Rates content with one to five stars. */
    void rate(ViewerId viewerId, ContentId contentId, StarRating stars);

    /** Adds content to viewer watchlist. */
    void addToWatchlist(ViewerId viewerId, ContentId contentId);

    /** Removes content from viewer watchlist. */
    void removeFromWatchlist(ViewerId viewerId, ContentId contentId);

    /** Removes deleted content from every watchlist. */
    void removeFromWatchlists(ContentId contentId);

    /** Retrieves all items from viewer watchlist as content responses. */
    List<RecommendationResponse> getWatchlist(ViewerId viewerId);

    /** Get rating that viewer gave to specific content. */
    java.util.Optional<Rating> getUserRatingForContent(ViewerId viewerId, ContentId contentId);

    boolean isInWatchlist(ViewerId viewerId, ContentId contentId);
}
