package com.project.youtlix.unit.contentlibrary.application;

import com.project.youtlix.contentlibrary.application.port.in.ContentMetadata;
import com.project.youtlix.contentlibrary.application.service.ContentLibraryApplicationService;
import com.project.youtlix.contentlibrary.domain.model.Duration;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Page;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.RecordingDomainEventPublisher;
import com.project.youtlix.testsupport.fixture.memory.InMemoryContentRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class ContentLibraryUseCaseUnitTest {

    @Test
    void createMovieMakesItBrowsableAndExposesCatalogMetadata() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        RecordingDomainEventPublisher publisher = new RecordingDomainEventPublisher();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, publisher);
        Metadata metadata = new Metadata(
                "DDD Movie",
                "Architecture",
                "thumb",
                Genre.DOCUMENTARY,
                2026,
                List.of(new Keyword("architecture"))
        );

        var id = service.createMovie(metadata, Duration.ofSeconds(3600), new VideoFile("cdn://ddd", List.of("en")));
        ContentMetadata catalogMetadata = service.metadataOf(id);

        assertThat(service.browse(new Page(0, 10))).hasSize(1);
        assertThat(catalogMetadata.title()).isEqualTo("DDD Movie");
        assertThat(service.videoFileOf(id).uri()).isEqualTo("cdn://ddd");
        assertThat(publisher.events()).isNotEmpty();
    }
}
