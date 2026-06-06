package com.project.youtlix.recommendation.infrastructure.out.persistence;

import com.project.youtlix.recommendation.application.port.out.RatingRepository;
import com.project.youtlix.recommendation.domain.model.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Temporary in-memory adapter for ratings until SQL persistence is implemented. */
@Repository
public class InMemoryRatingRepository implements RatingRepository {
    private final Map<RatingId, Rating> ratings = new ConcurrentHashMap<>();
    @Override public void save(Rating rating) { ratings.put(rating.id(), rating); }
    @Override public Optional<Rating> ofViewerAndContent(ViewerId viewerId, ContentId contentId) {
        return ratings.values().stream().filter(r -> r.viewerId().equals(viewerId) && r.contentId().equals(contentId)).findFirst();
    }
    @Override public List<Rating> ofViewer(ViewerId viewerId) { return ratings.values().stream().filter(r -> r.viewerId().equals(viewerId)).toList(); }
}
