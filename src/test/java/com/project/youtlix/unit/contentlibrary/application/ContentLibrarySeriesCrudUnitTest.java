package com.project.youtlix.unit.contentlibrary.application;

import com.project.youtlix.contentlibrary.application.service.ContentLibraryApplicationService;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.event.ContentRemoved;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentRequest;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentResponse;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentType;
import com.project.youtlix.contentlibrary.infrastructure.in.web.EpisodeRequest;
import com.project.youtlix.contentlibrary.infrastructure.in.web.SeasonRequest;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentController;
import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.NoOpDomainEventPublisher;
import com.project.youtlix.testsupport.fixture.RecordingDomainEventPublisher;
import com.project.youtlix.testsupport.fixture.RoleIdentityProvider;
import com.project.youtlix.testsupport.fixture.memory.InMemoryContentRepository;
import com.project.youtlix.testsupport.fixture.stub.NoOpRecommendationUseCase;
import com.project.youtlix.authentication.domain.model.Role;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class ContentLibrarySeriesCrudUnitTest {

    private static final String TEST_MARKER = "viewer-series-unit-" + UUID.randomUUID();

    @Test
    void seriesLifecycle_createReadUpdateReadDeleteVerifyRemoved() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        RecordingDomainEventPublisher publisher = new RecordingDomainEventPublisher();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, publisher);
        ContentController controller = new ContentController(
                service,
                new NoOpRecommendationUseCase(),
                new RoleIdentityProvider(Role.LIBRARY_ADMIN)
        );

        UUID seriesId = controller.create("Bearer jwt", new ContentRequest(
                ContentType.SERIES,
                TEST_MARKER + "-original",
                "Series description",
                "thumb",
                Genre.DRAMA,
                2020,
                List.of("drama"),
                null,
                null,
                List.of()
        ));
        UUID seasonId = controller.addSeason("Bearer jwt", seriesId, new SeasonRequest(1, "Season 1"));
        UUID episodeId = controller.addEpisode("Bearer jwt", seriesId, seasonId, new EpisodeRequest(
                1,
                "Pilot",
                1800,
                "cdn://" + TEST_MARKER + "/ep1",
                List.of("pl")
        ));

        ContentResponse afterCreate = controller.browse("Bearer jwt", 1, 20).contents().getFirst();
        assertThat(afterCreate.title()).isEqualTo(TEST_MARKER + "-original");
        assertThat(afterCreate.seasons()).hasSize(1);

        controller.update("Bearer jwt", seriesId, new ContentRequest(
                ContentType.SERIES,
                TEST_MARKER + "-updated",
                "Updated description",
                "thumb-updated",
                Genre.DRAMA,
                2020,
                List.of("drama"),
                null,
                null,
                List.of()
        ));

        ContentResponse afterUpdate = controller.browse("Bearer jwt", 1, 20).contents().getFirst();
        assertThat(afterUpdate.title()).isEqualTo(TEST_MARKER + "-updated");
        assertThat(afterUpdate.seasons().getFirst().episodes().getFirst().id()).isEqualTo(episodeId);

        publisher.clear();
        controller.delete("Bearer jwt", seriesId);

        assertThat(controller.browse("Bearer jwt", 1, 20).contents()).isEmpty();
        assertThat(repository.contains(new com.project.youtlix.contentlibrary.domain.model.ContentId(seriesId))).isFalse();
        assertThat(publisher.events())
                .anyMatch(event -> event instanceof ContentRemoved);
    }
}
