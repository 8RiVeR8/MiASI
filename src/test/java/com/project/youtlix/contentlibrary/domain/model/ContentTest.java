package com.project.youtlix.contentlibrary.domain.model;

import com.project.youtlix.contentlibrary.domain.model.event.ContentAdded;
import com.project.youtlix.contentlibrary.domain.model.event.ContentModified;
import com.project.youtlix.contentlibrary.domain.model.event.ContentRemoved;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ContentTest {

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
        return new Metadata(title, "Description", ContentType.MOVIE, "thumb", Genre.ACTION, 2025, List.of(new Keyword("hero")));
    }
}
