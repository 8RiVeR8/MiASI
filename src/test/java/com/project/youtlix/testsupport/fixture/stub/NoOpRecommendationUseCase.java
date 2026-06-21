package com.project.youtlix.testsupport.fixture.stub;

import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.Rating;
import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.RecommendationResponse;
import com.project.youtlix.recommendation.domain.model.StarRating;
import com.project.youtlix.recommendation.domain.model.ViewerId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class NoOpRecommendationUseCase implements RecommendationUseCase {

    @Override
    public RecommendationList generateFor(ViewerId viewerId) {
        return new RecommendationList(viewerId, Instant.now(), List.of());
    }

    @Override
    public List<RecommendationResponse> toContentResponses(RecommendationList recommendations) {
        return List.of();
    }

    @Override
    public void rate(ViewerId viewerId, ContentId contentId, StarRating stars) {
    }

    @Override
    public void addToWatchlist(ViewerId viewerId, ContentId contentId) {
    }

    @Override
    public void removeFromWatchlist(ViewerId viewerId, ContentId contentId) {
    }

    @Override
    public void removeFromWatchlists(ContentId contentId) {
    }

    @Override
    public List<RecommendationResponse> getWatchlist(ViewerId viewerId) {
        return List.of();
    }

    @Override
    public Optional<Rating> getUserRatingForContent(ViewerId viewerId, ContentId contentId) {
        return Optional.empty();
    }

    @Override
    public boolean isInWatchlist(ViewerId viewerId, ContentId contentId) {
        return false;
    }
}
