package com.project.youtlix.unit.recommendation.domain;

import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.recommendation.domain.model.Watchlist;
import com.project.youtlix.recommendation.domain.model.WatchlistId;
import com.project.youtlix.recommendation.domain.model.event.AddedToWatchlist;
import com.project.youtlix.testsupport.annotation.UnitTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class WatchlistUnitTest {

    @Test
    void addEmitsEventAndRemoveClearsItem() {
        Watchlist watchlist = new Watchlist(WatchlistId.newId(), new ViewerId(UUID.randomUUID()));
        ContentId contentId = ContentId.newId();

        watchlist.add(contentId);

        assertThat(watchlist.contains(contentId)).isTrue();
        assertThat(watchlist.occurredEvents()).hasSize(1).first().isInstanceOf(AddedToWatchlist.class);

        watchlist.remove(contentId);

        assertThat(watchlist.isEmpty()).isTrue();
        assertThat(watchlist.contains(contentId)).isFalse();
    }

    @Test
    void duplicateAddIsIgnored() {
        Watchlist watchlist = new Watchlist(WatchlistId.newId(), new ViewerId(UUID.randomUUID()));
        ContentId contentId = ContentId.newId();

        watchlist.add(contentId);
        watchlist.add(contentId);

        assertThat(watchlist.items()).hasSize(1);
        assertThat(watchlist.occurredEvents()).hasSize(1);
    }
}
