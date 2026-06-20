package com.project.youtlix.testsupport.fixture.memory;

import com.project.youtlix.recommendation.application.port.out.RatingRepository;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.Rating;
import com.project.youtlix.recommendation.domain.model.ViewerId;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory rating repository isolated from Supabase.
 */
public final class InMemoryRatingRepository implements RatingRepository {

    private final Map<String, Rating> ratings = new LinkedHashMap<>();

    @Override
    public void save(Rating rating) {
        ratings.put(key(rating.viewerId(), rating.contentId()), rating);
    }

    @Override
    public Optional<Rating> ofViewerAndContent(ViewerId viewerId, ContentId contentId) {
        return Optional.ofNullable(ratings.get(key(viewerId, contentId)));
    }

    @Override
    public List<Rating> ofViewer(ViewerId viewerId) {
        return ratings.values().stream()
                .filter(rating -> rating.viewerId().equals(viewerId))
                .toList();
    }

    private String key(ViewerId viewerId, ContentId contentId) {
        return viewerId.value() + ":" + contentId.value();
    }
}
