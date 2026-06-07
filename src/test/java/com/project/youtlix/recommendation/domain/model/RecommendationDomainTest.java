package com.project.youtlix.recommendation.domain.model;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecommendationDomainTest {

    @Test
    void starRatingAcceptsOnlyOneToFiveRange() {
        assertThat(new StarRating(5).isPositive()).isTrue();
        assertThat(new StarRating(3).isPositive()).isFalse();
        assertThatThrownBy(() -> new StarRating(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new StarRating(6)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void watchlistDoesNotDuplicateContent() {
        Watchlist watchlist = new Watchlist(WatchlistId.newId(), new ViewerId(UUID.randomUUID()));
        ContentId contentId = ContentId.newId();

        watchlist.add(contentId);
        watchlist.add(contentId);

        assertThat(watchlist.items()).hasSize(1);
        assertThat(watchlist.contains(contentId)).isTrue();
    }
}
