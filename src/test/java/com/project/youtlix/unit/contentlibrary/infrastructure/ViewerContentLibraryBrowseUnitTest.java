package com.project.youtlix.unit.contentlibrary.infrastructure;

import com.project.youtlix.authentication.domain.model.Role;
import com.project.youtlix.authentication.domain.model.UserIdentity;
import com.project.youtlix.contentlibrary.application.service.ContentLibraryApplicationService;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentController;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentRequest;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentResponse;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentType;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.RecommendationReason;
import com.project.youtlix.recommendation.domain.model.RecommendedItem;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.ViewerTestAccount;
import com.project.youtlix.testsupport.fixture.FixedIdentityProvider;
import com.project.youtlix.testsupport.fixture.RecordingDomainEventPublisher;
import com.project.youtlix.testsupport.fixture.memory.InMemoryContentRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the viewer test identity can browse content created in the same isolated test.
 * Write operations use a temporary admin identity; only in-memory data is affected.
 */
@UnitTest
class ViewerContentLibraryBrowseUnitTest {

    private static final String TEST_MARKER = "viewer-browse-unit-" + UUID.randomUUID();

    @Test
    void viewerReadsMovieCreatedInIsolatedCatalog() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(
                repository,
                new RecordingDomainEventPublisher()
        );
        FixedIdentityProvider viewerIdentity = new FixedIdentityProvider(ViewerTestAccount.viewerIdentity());
        FixedIdentityProvider adminIdentity = new FixedIdentityProvider(
                new UserIdentity(
                        new com.project.youtlix.authentication.domain.model.ViewerId(UUID.randomUUID()),
                        Role.LIBRARY_ADMIN
                )
        );
        ContentController controller = new ContentController(service, noRecommendations(), viewerIdentity);
        ContentController adminController = new ContentController(service, noRecommendations(), adminIdentity);

        UUID movieId = adminController.create(ViewerTestAccount.BEARER, new ContentRequest(
                ContentType.MOVIE,
                TEST_MARKER,
                "Readable by viewer",
                "thumb",
                Genre.COMEDY,
                2026,
                List.of("unit-test"),
                1800,
                "cdn://" + TEST_MARKER,
                List.of("pl")
        ));

        ContentController.LibraryPageResponse page = controller.browse(ViewerTestAccount.BEARER, 1, 20);
        List<ContentResponse> searchResults = controller.search(ViewerTestAccount.BEARER, TEST_MARKER);

        assertThat(page.contents())
                .anySatisfy(content -> {
                    assertThat(content.id()).isEqualTo(movieId);
                    assertThat(content.title()).isEqualTo(TEST_MARKER);
                });
        assertThat(searchResults).singleElement()
                .extracting(ContentResponse::id)
                .isEqualTo(movieId);

        adminController.delete(ViewerTestAccount.BEARER, movieId);

        assertThat(controller.browse(ViewerTestAccount.BEARER, 1, 20).contents())
                .noneMatch(content -> content.id().equals(movieId));
        assertThat(repository.contains(new com.project.youtlix.contentlibrary.domain.model.ContentId(movieId))).isFalse();
    }

    private RecommendationUseCase noRecommendations() {
        return new RecommendationUseCase() {
            @Override
            public RecommendationList generateFor(ViewerId viewerId) {
                assertThat(viewerId.value()).isEqualTo(ViewerTestAccount.VIEWER_ID);
                return new RecommendationList(viewerId, Instant.now(), List.of(
                        new RecommendedItem(
                                com.project.youtlix.recommendation.domain.model.ContentId.newId(),
                                0.5,
                                RecommendationReason.GLOBAL_POPULARITY
                        )
                ));
            }

            @Override
            public List<ContentResponse> toContentResponses(RecommendationList recommendations) {
                return List.of();
            }

            @Override
            public void rate(ViewerId viewerId, com.project.youtlix.recommendation.domain.model.ContentId contentId, com.project.youtlix.recommendation.domain.model.StarRating stars) {
            }

            @Override
            public void addToWatchlist(ViewerId viewerId, com.project.youtlix.recommendation.domain.model.ContentId contentId) {
            }

            @Override
            public void removeFromWatchlist(ViewerId viewerId, com.project.youtlix.recommendation.domain.model.ContentId contentId) {
            }

            @Override
            public void removeFromWatchlists(com.project.youtlix.recommendation.domain.model.ContentId contentId) {
            }
        };
    }
}
