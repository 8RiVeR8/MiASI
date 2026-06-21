package com.project.youtlix.integration.recommendation;

import com.project.youtlix.contentlibrary.application.port.out.ContentRepository;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.ContentType;
import com.project.youtlix.contentlibrary.domain.model.Duration;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Movie;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.integration.support.IntegrationTestSupport;
import com.project.youtlix.recommendation.application.port.out.WatchlistRepository;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.recommendation.domain.model.Watchlist;
import com.project.youtlix.recommendation.domain.model.WatchlistId;
import com.project.youtlix.testsupport.annotation.IntegrationTest;
import com.project.youtlix.testsupport.fixture.ViewerTestAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class SupabaseWatchlistRepositoryIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ViewerId viewerId;
    private ContentId libraryContentId;

    @AfterEach
    void cleanup() {
        if (viewerId != null) {
            jdbcTemplate.update("delete from recommendation.watchlist_items where watchlist_id in (select id from recommendation.watchlists where viewer_id = ?)", viewerId.value());
            jdbcTemplate.update("delete from recommendation.watchlists where viewer_id = ?", viewerId.value());
        }
        if (libraryContentId != null) {
            contentRepository.remove(libraryContentId);
        }
    }

    @Test
    void savesAndLoadsWatchlistWithItem() {
        viewerId = ViewerTestAccount.recommendationViewerId();
        String marker = integrationMarker();
        Movie movie = new Movie(
                ContentId.newId(),
                new Metadata(marker, "Watchlist IT", ContentType.MOVIE, "thumb", Genre.COMEDY, 2026, List.of(new Keyword("it"))),
                Duration.ofSeconds(120),
                new VideoFile("cdn://" + marker, List.of("pl"))
        );
        movie.publish();
        contentRepository.save(movie);
        libraryContentId = movie.id();

        com.project.youtlix.recommendation.domain.model.ContentId contentId =
                new com.project.youtlix.recommendation.domain.model.ContentId(libraryContentId.value());
        Watchlist watchlist = new Watchlist(WatchlistId.newId(), viewerId);
        watchlist.add(contentId);

        watchlistRepository.save(watchlist);

        assertThat(watchlistRepository.ofViewer(viewerId))
                .isPresent()
                .get()
                .satisfies(found -> assertThat(found.contains(contentId)).isTrue());
    }
}
