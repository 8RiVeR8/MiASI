package com.project.youtlix.unit.recommendation.domain;

import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.Rating;
import com.project.youtlix.recommendation.domain.model.RatingId;
import com.project.youtlix.recommendation.domain.model.StarRating;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.recommendation.domain.service.RatingService;
import com.project.youtlix.testsupport.annotation.UnitTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class RatingServiceUnitTest {

    private final RatingService ratingService = new RatingService();

    @Test
    void createsNewRatingWhenNoneExists() {
        ViewerId viewerId = new ViewerId(UUID.randomUUID());
        ContentId contentId = ContentId.newId();

        Rating rating = ratingService.rate(Optional.empty(), viewerId, contentId, new StarRating(5));

        assertThat(rating.viewerId()).isEqualTo(viewerId);
        assertThat(rating.contentId()).isEqualTo(contentId);
        assertThat(rating.stars().value()).isEqualTo(5);
    }

    @Test
    void updatesExistingRating() {
        Rating existing = new Rating(
                RatingId.newId(),
                new ViewerId(UUID.randomUUID()),
                ContentId.newId(),
                new StarRating(2),
                Instant.parse("2026-01-01T00:00:00Z"),
                false
        );

        Rating updated = ratingService.rate(Optional.of(existing), existing.viewerId(), existing.contentId(), new StarRating(4));

        assertThat(updated).isSameAs(existing);
        assertThat(updated.stars().value()).isEqualTo(4);
    }
}
