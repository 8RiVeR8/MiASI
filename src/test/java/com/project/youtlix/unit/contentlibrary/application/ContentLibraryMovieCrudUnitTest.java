package com.project.youtlix.unit.contentlibrary.application;

import com.project.youtlix.contentlibrary.application.port.in.ContentMetadata;
import com.project.youtlix.contentlibrary.application.port.in.ContentNotFoundException;
import com.project.youtlix.contentlibrary.application.service.ContentLibraryApplicationService;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Isolated movie CRUD lifecycle. Uses in-memory storage only — nothing is written to Supabase.
 */
@UnitTest
class ContentLibraryMovieCrudUnitTest {

    private static final String TEST_MARKER = "viewer-crud-unit-" + UUID.randomUUID();

    @Test
    void movieLifecycle_createReadUpdateReadDeleteVerifyRemoved() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        RecordingDomainEventPublisher publisher = new RecordingDomainEventPublisher();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, publisher);

        Metadata originalMetadata = new Metadata(
                TEST_MARKER + "-original",
                "Original description",
                "thumb-original",
                Genre.DOCUMENTARY,
                2026,
                List.of(new Keyword("unit-test"))
        );
        Metadata updatedMetadata = new Metadata(
                TEST_MARKER + "-updated",
                "Updated description",
                "thumb-updated",
                Genre.DOCUMENTARY,
                2026,
                List.of(new Keyword("unit-test-updated"))
        );
        VideoFile originalVideo = new VideoFile("cdn://" + TEST_MARKER + "/original", List.of("pl"));
        VideoFile updatedVideo = new VideoFile("cdn://" + TEST_MARKER + "/updated", List.of("pl", "en"));

        ContentId createdId = service.createMovie(
                originalMetadata,
                Duration.ofSeconds(1200),
                originalVideo
        );

        ContentMetadata afterCreate = service.metadataOf(createdId);
        assertThat(afterCreate.title()).isEqualTo(originalMetadata.title());
        assertThat(service.browse(new Page(0, 20)))
                .anySatisfy(content -> assertThat(content.id()).isEqualTo(createdId));
        assertThat(service.searchByKeyword(TEST_MARKER))
                .extracting(content -> content.metadata().title())
                .contains(originalMetadata.title());
        assertThat(repository.contains(createdId)).isTrue();

        service.updateMovie(
                createdId,
                updatedMetadata,
                Duration.ofSeconds(1500),
                updatedVideo
        );

        ContentMetadata afterUpdate = service.metadataOf(createdId);
        assertThat(afterUpdate.title()).isEqualTo(updatedMetadata.title());
        assertThat(service.searchByKeyword(TEST_MARKER + "-updated"))
                .extracting(content -> content.metadata().title())
                .containsExactly(updatedMetadata.title());

        service.remove(createdId);

        assertThat(repository.contains(createdId)).isFalse();
        assertThat(service.browse(new Page(0, 20)))
                .noneMatch(content -> content.id().equals(createdId));
        assertThatThrownBy(() -> service.metadataOf(createdId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(createdId.value().toString());
        assertThatThrownBy(() -> service.remove(createdId))
                .isInstanceOf(ContentNotFoundException.class);
    }
}
