package com.project.youtlix.unit.recommendation.domain;

import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.Rating;
import com.project.youtlix.recommendation.domain.model.RatingId;
import com.project.youtlix.recommendation.domain.model.StarRating;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.recommendation.domain.model.event.ContentRated;
import com.project.youtlix.testsupport.annotation.UnitTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class RatingUnitTest {

    @Test
    void newRatingEmitsContentRatedEvent() {
        ViewerId viewerId = new ViewerId(UUID.randomUUID());
        ContentId contentId = ContentId.newId();

        Rating rating = new Rating(
                RatingId.newId(),
                viewerId,
                contentId,
                new StarRating(5),
                Instant.parse("2026-01-01T00:00:00Z")
        );

        assertThat(rating.stars().value()).isEqualTo(5);
        assertThat(rating.occurredEvents()).hasSize(1).first()
                .isInstanceOfSatisfying(ContentRated.class, event -> {
                    assertThat(event.viewerId()).isEqualTo(viewerId);
                    assertThat(event.contentId()).isEqualTo(contentId);
                    assertThat(event.stars().value()).isEqualTo(5);
                });
    }

    @Test
    void changeToUpdatesStarsAndEmitsAnotherEvent() {
        Rating rating = new Rating(
                RatingId.newId(),
                new ViewerId(UUID.randomUUID()),
                ContentId.newId(),
                new StarRating(2),
                Instant.parse("2026-01-01T00:00:00Z"),
                false
        );

        rating.changeTo(new StarRating(4));

        assertThat(rating.stars().value()).isEqualTo(4);
        assertThat(rating.occurredEvents()).hasSize(1).first().isInstanceOf(ContentRated.class);
    }
}
