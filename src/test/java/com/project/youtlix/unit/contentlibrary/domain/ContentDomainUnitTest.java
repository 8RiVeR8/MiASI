package com.project.youtlix.unit.contentlibrary.domain;

import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Duration;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Movie;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.contentlibrary.domain.model.event.ContentAdded;
import com.project.youtlix.contentlibrary.domain.model.event.ContentModified;
import com.project.youtlix.contentlibrary.domain.model.event.ContentRemoved;
import com.project.youtlix.testsupport.annotation.UnitTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class ContentDomainUnitTest {

    @Test
    void movieCanBePublishedUpdatedAndWithdrawn() {
        Movie movie = new Movie(
                ContentId.newId(),
                metadata("Original"),
                Duration.ofSeconds(600),
                new VideoFile("cdn://movie", List.of("pl"))
        );

        movie.publish();
        movie.updateMetadata(metadata("Updated"));
        movie.withdraw();

        assertThat(movie.available()).isFalse();
        assertThat(movie.metadata().title()).isEqualTo("Updated");
        assertThat(movie.occurredEvents())
                .hasAtLeastOneElementOfType(ContentAdded.class)
                .hasAtLeastOneElementOfType(ContentModified.class)
                .hasAtLeastOneElementOfType(ContentRemoved.class);
    }

    private Metadata metadata(String title) {
        return new Metadata(title, "Description", "thumb", Genre.ACTION, 2025, List.of(new Keyword("hero")));
    }
}
