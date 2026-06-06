package com.project.youtlix.recommendation.domain.model;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for rating and watchlist domain objects from PU12-PU13. */
class RecommendationDomainTest {
    @Test
    void starRatingAcceptsOnlyOneToFiveRange() {
        assertTrue(new StarRating(5).isPositive());
        assertThrows(IllegalArgumentException.class, () -> new StarRating(0));
        assertThrows(IllegalArgumentException.class, () -> new StarRating(6));
    }

    @Test
    void watchlistDoesNotDuplicateContent() {
        Watchlist watchlist = Watchlist.emptyFor(new ViewerId(UUID.randomUUID()));
        ContentId contentId = new ContentId(UUID.randomUUID());
        watchlist.add(contentId);
        watchlist.add(contentId);
        assertEquals(1, watchlist.items().size());
    }
}
