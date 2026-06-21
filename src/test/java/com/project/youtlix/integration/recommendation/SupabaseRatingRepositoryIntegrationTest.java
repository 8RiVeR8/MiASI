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
import com.project.youtlix.recommendation.application.port.out.RatingRepository;
import com.project.youtlix.recommendation.domain.model.Rating;
import com.project.youtlix.recommendation.domain.model.RatingId;
import com.project.youtlix.recommendation.domain.model.StarRating;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.testsupport.annotation.IntegrationTest;
import com.project.youtlix.testsupport.fixture.ViewerTestAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class SupabaseRatingRepositoryIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ViewerId viewerId;
    private ContentId libraryContentId;
    private UUID recommendationContentId;

    @AfterEach
    void cleanup() {
        if (viewerId != null && recommendationContentId != null) {
            jdbcTemplate.update(
                    "delete from recommendation.ratings where viewer_id = ? and content_id = ?",
                    viewerId.value(),
                    recommendationContentId
            );
        }
        if (libraryContentId != null) {
            contentRepository.remove(libraryContentId);
        }
    }

    @Test
    void savesAndLoadsRating() {
        viewerId = ViewerTestAccount.recommendationViewerId();
        String marker = integrationMarker();
        Movie movie = new Movie(
                ContentId.newId(),
                new Metadata(marker, "Rating IT", ContentType.MOVIE, "thumb", Genre.DOCUMENTARY, 2026, List.of(new Keyword("it"))),
                Duration.ofSeconds(120),
                new VideoFile("cdn://" + marker, List.of("pl"))
        );
        movie.publish();
        contentRepository.save(movie);
        libraryContentId = movie.id();
        recommendationContentId = libraryContentId.value();

        Rating rating = new Rating(
                RatingId.newId(),
                viewerId,
                new com.project.youtlix.recommendation.domain.model.ContentId(recommendationContentId),
                new StarRating(4),
                Instant.parse("2026-06-20T12:00:00Z"),
                false
        );

        ratingRepository.save(rating);

        assertThat(ratingRepository.ofViewerAndContent(viewerId, new com.project.youtlix.recommendation.domain.model.ContentId(recommendationContentId)))
                .isPresent()
                .get()
                .extracting(found -> found.stars().value())
                .isEqualTo(4);
    }
}
