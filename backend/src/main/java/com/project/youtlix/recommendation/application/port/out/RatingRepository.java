package com.project.youtlix.recommendation.application.port.out;

import com.project.youtlix.recommendation.domain.model.*;
import java.util.List;
import java.util.Optional;

/** Output port for rating persistence. */
public interface RatingRepository {
    void save(Rating rating);
    Optional<Rating> ofViewerAndContent(ViewerId viewerId, ContentId contentId);
    List<Rating> ofViewer(ViewerId viewerId);
}
