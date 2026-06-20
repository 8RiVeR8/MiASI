package com.project.youtlix.unit.recommendation.domain;

import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.StarRating;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.recommendation.domain.model.Watchlist;
import com.project.youtlix.recommendation.domain.model.WatchlistId;
import com.project.youtlix.testsupport.annotation.UnitTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UnitTest
class RecommendationDomainUnitTest {

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
