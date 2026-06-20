package com.project.youtlix.testsupport.fixture.stub;

import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentResponse;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.StarRating;
import com.project.youtlix.recommendation.domain.model.ViewerId;

import java.time.Instant;
import java.util.List;

public class NoOpRecommendationUseCase implements RecommendationUseCase {

    @Override
    public RecommendationList generateFor(ViewerId viewerId) {
        return new RecommendationList(viewerId, Instant.now(), List.of());
    }

    @Override
    public List<ContentResponse> toContentResponses(RecommendationList recommendations) {
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
}
