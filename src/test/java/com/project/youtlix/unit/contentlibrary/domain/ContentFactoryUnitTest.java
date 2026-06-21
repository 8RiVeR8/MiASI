package com.project.youtlix.unit.contentlibrary.domain;

import com.project.youtlix.contentlibrary.domain.model.Duration;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Movie;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.contentlibrary.domain.service.ContentFactory;
import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.MetadataFixtures;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class ContentFactoryUnitTest {

    private final ContentFactory factory = new ContentFactory();

    @Test
    void createMoviePublishesAggregate() {
        Metadata metadata = MetadataFixtures.movie(
                "Factory Movie",
                "Created by factory",
                "thumb",
                Genre.DOCUMENTARY,
                2026,
                List.of(new Keyword("factory"))
        );

        Movie movie = factory.createMovie(metadata, Duration.ofSeconds(90), new VideoFile("cdn://factory", List.of("pl")));

        assertThat(movie.available()).isTrue();
        assertThat(movie.metadata().title()).isEqualTo("Factory Movie");
        assertThat(movie.occurredEvents()).isNotEmpty();
    }
}
