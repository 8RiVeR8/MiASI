package com.project.youtlix.contentlibrary.domain.model;

import com.project.youtlix.contentlibrary.domain.service.ContentFactory;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for content aggregates from PU8-PU10. */
class ContentTest {
    @Test
    void movieCanBePublishedUpdatedAndWithdrawn() {
        ContentFactory factory = new ContentFactory();
        Metadata metadata = new Metadata("Movie", "Description", "thumb.jpg", Genre.DRAMA, 2026, List.of(new Keyword("drama")));
        Movie movie = factory.createMovie(metadata, new Duration(120), new VideoFile("cdn://movie", List.of("pl")));

        assertTrue(movie.available());
        movie.updateMetadata(new Metadata("Movie 2", "Description", "thumb.jpg", Genre.DRAMA, 2026, List.of()));
        movie.withdraw();

        assertFalse(movie.available());
        assertTrue(movie.occurredEvents().size() >= 3);
    }
}
