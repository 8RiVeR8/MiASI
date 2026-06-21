package com.project.youtlix.integration.contentlibrary;

import com.project.youtlix.contentlibrary.application.port.out.ContentRepository;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Duration;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Movie;
import com.project.youtlix.contentlibrary.domain.model.ContentType;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.integration.support.IntegrationTestSupport;
import com.project.youtlix.testsupport.annotation.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class SupabaseContentRepositoryIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ContentRepository contentRepository;

    private ContentId createdId;

    @AfterEach
    void cleanup() {
        if (createdId != null) {
            contentRepository.remove(createdId);
        }
    }

    @Test
    void savesAndLoadsMovieAggregate() {
        String marker = integrationMarker();
        Movie movie = new Movie(
                ContentId.newId(),
                new Metadata(marker, "Integration movie", ContentType.MOVIE, "thumb", Genre.DOCUMENTARY, 2026, List.of(new Keyword("it"))),
                Duration.ofSeconds(120),
                new VideoFile("cdn://" + marker, List.of("pl"))
        );
        movie.publish();

        contentRepository.save(movie);
        createdId = movie.id();

        assertThat(contentRepository.ofId(createdId))
                .isPresent()
                .get()
                .satisfies(content -> assertThat(content.metadata().title()).isEqualTo(marker));
    }
}
