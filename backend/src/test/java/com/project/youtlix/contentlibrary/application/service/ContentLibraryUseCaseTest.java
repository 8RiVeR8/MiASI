package com.project.youtlix.contentlibrary.application.service;

import com.project.youtlix.common.infrastructure.event.InMemoryDomainEventBus;
import com.project.youtlix.contentlibrary.application.port.in.CreateMovieCommand;
import com.project.youtlix.contentlibrary.domain.model.*;
import com.project.youtlix.contentlibrary.domain.service.ContentFactory;
import com.project.youtlix.contentlibrary.domain.service.ContentSearchService;
import com.project.youtlix.contentlibrary.infrastructure.out.persistence.InMemoryContentRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** Application tests for PU5-PU10 content library flows. */
class ContentLibraryUseCaseTest {
    @Test
    void createMovieMakesItBrowsableAndExposesCatalogMetadata() {
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(
                new InMemoryContentRepository(), new ContentFactory(), new ContentSearchService(), new InMemoryDomainEventBus());
        Metadata metadata = new Metadata("Catalog Movie", "Description", "thumb.jpg", Genre.ACTION, 2026, List.of(new Keyword("action")));

        ContentId id = service.createMovie(new CreateMovieCommand(metadata, new Duration(90), new VideoFile("cdn://movie", List.of("en"))));

        assertEquals(1, service.browse(new Page(0, 10)).size());
        assertEquals("Catalog Movie", service.metadataOf(id).title());
    }
}
