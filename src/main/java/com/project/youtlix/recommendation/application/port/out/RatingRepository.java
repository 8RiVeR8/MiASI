package com.project.youtlix.recommendation.application.port.out;

import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.Rating;
import com.project.youtlix.recommendation.domain.model.ViewerId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for rating aggregates.
 */
public interface RatingRepository {

    /** Persists a rating. */
    void save(Rating rating);

    /** Loads one rating for viewer and content. */
    Optional<Rating> ofViewerAndContent(ViewerId viewerId, ContentId contentId);

    /** Loads all ratings for viewer. */
    List<Rating> ofViewer(ViewerId viewerId);
}
