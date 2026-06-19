package com.project.youtlix.system;

import com.project.youtlix.authentication.application.port.out.IdentityProvider;
import com.project.youtlix.authentication.domain.model.Role;
import com.project.youtlix.authentication.domain.model.UserIdentity;
import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import com.project.youtlix.contentlibrary.application.port.in.ResolvedPlayable;
import com.project.youtlix.contentlibrary.application.port.out.ContentRepository;
import com.project.youtlix.contentlibrary.application.service.ContentLibraryApplicationService;
import com.project.youtlix.contentlibrary.domain.model.Content;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Duration;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Movie;
import com.project.youtlix.contentlibrary.domain.model.Page;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;
import com.project.youtlix.contentlibrary.domain.model.Series;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.contentlibrary.domain.model.event.ContentAdded;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentController;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentRequest;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentResponse;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.domain.model.RecommendationReason;
import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.RecommendedItem;
import com.project.youtlix.recommendation.domain.model.StarRating;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContentLibrarySystemTest {

    @Test
    void contentCreationPathRunsFromWebAdapterToRepositoryPort() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        RecordingPublisher publisher = new RecordingPublisher();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, publisher);
        FixedIdentityProvider identityProvider = new FixedIdentityProvider(Role.LIBRARY_ADMIN);
        RecordingRecommendations recommendations = new RecordingRecommendations();
        ContentController controller = new ContentController(
                service,
                recommendations,
                identityProvider
        );

        controller.create("Bearer jwt", new ContentRequest(
                "Clean Architecture",
                "System test path",
                "thumb",
                Genre.DOCUMENTARY,
                2026,
                List.of("architecture"),
                2400,
                "cdn://clean-architecture",
                List.of("pl")
        ));

        ContentController.LibraryPageResponse response = controller.browse("Bearer jwt", 1, 20);
        List<ContentResponse> contents = response.contents();

        assertThat(contents).hasSize(1);
        assertThat(contents.getFirst().type()).isEqualTo("MOVIE");
        assertThat(contents.getFirst().title()).isEqualTo("Clean Architecture");
        assertThat(contents.getFirst().thumbnailUrl()).isEqualTo("thumb");
        assertThat(publisher.events)
                .anySatisfy(event -> assertThat(event)
                        .isInstanceOfSatisfying(ContentAdded.class, added ->
                                assertThat(added.title()).isEqualTo("Clean Architecture")));
        assertThat(response.pagination().page()).isEqualTo(1);
        assertThat(response.pagination().size()).isEqualTo(20);
        assertThat(response.pagination().itemCount()).isEqualTo(1);
        assertThat(response.recommendations()).hasSize(1);
        assertThat(recommendations.requestedViewerId).isEqualTo(identityProvider.viewerId().value());
    }

    @Test
    void contentSearchPathRunsFromWebAdapterThroughUseCaseAndApplicationService() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, new NoOpPublisher());
        ContentController controller = new ContentController(
                service,
                new NoRecommendations(),
                new FixedIdentityProvider(Role.VIEWER)
        );

        service.createMovie(new Metadata(
                "Clean Architecture",
                "Hexagonal architecture documentary",
                "thumb-clean",
                Genre.DOCUMENTARY,
                2026,
                List.of(new Keyword("Architecture"))
        ), Duration.ofSeconds(2400), new VideoFile("cdn://clean-architecture", List.of("pl")));
        service.createMovie(new Metadata(
                "Comedy Night",
                "Stand-up special",
                "thumb-comedy",
                Genre.COMEDY,
                2024,
                List.of(new Keyword("standup"))
        ), Duration.ofSeconds(3600), new VideoFile("cdn://comedy-night", List.of("pl")));

        List<ContentResponse> response = controller.search("Bearer jwt", " ARCHITECTURE ");

        assertThat(response).singleElement()
                .extracting(ContentResponse::title)
                .isEqualTo("Clean Architecture");
        assertThat(repository.lastKeywordPhrase).isEqualTo("ARCHITECTURE");
    }

    @Test
    void contentSearchRejectsBlankPhrase() {
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(
                new InMemoryContentRepository(),
                new NoOpPublisher()
        );
        ContentController controller = new ContentController(
                service,
                new NoRecommendations(),
                new FixedIdentityProvider(Role.VIEWER)
        );

        assertThatThrownBy(() -> controller.search("Bearer jwt", " "))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void contentFilteringPathRunsFromWebAdapterThroughUseCaseAndApplicationService() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, new NoOpPublisher());
        ContentController controller = new ContentController(
                service,
                new NoRecommendations(),
                new FixedIdentityProvider(Role.VIEWER)
        );

        service.createMovie(new Metadata(
                "Clean Architecture",
                "Hexagonal architecture documentary",
                "thumb-clean",
                Genre.DOCUMENTARY,
                2026,
                List.of(new Keyword("architecture"))
        ), Duration.ofSeconds(2400), new VideoFile("cdn://clean-architecture", List.of("pl")));
        service.createMovie(new Metadata(
                "Comedy Night",
                "Stand-up special",
                "thumb-comedy",
                Genre.COMEDY,
                2024,
                List.of(new Keyword("standup"))
        ), Duration.ofSeconds(3600), new VideoFile("cdn://comedy-night", List.of("pl")));
        service.createMovie(new Metadata(
                "Old Documentary",
                "Older documentary",
                "thumb-old",
                Genre.DOCUMENTARY,
                2020,
                List.of(new Keyword("archive"))
        ), Duration.ofSeconds(1800), new VideoFile("cdn://old-documentary", List.of("pl")));
        service.createMovie(new Metadata(
                "Architecture Clean",
                "Same words but title does not start with filter phrase",
                "thumb-architecture-clean",
                Genre.DOCUMENTARY,
                2026,
                List.of(new Keyword("design"))
        ), Duration.ofSeconds(1800), new VideoFile("cdn://architecture-clean", List.of("pl")));

        List<ContentResponse> response = controller.filter("Bearer jwt", "architecture", Genre.DOCUMENTARY, 2025, 2026);

        assertThat(response).extracting(ContentResponse::title)
                .containsExactlyInAnyOrder("Clean Architecture", "Architecture Clean");
        assertThat(response)
                .extracting(ContentResponse::genre)
                .containsOnly(Genre.DOCUMENTARY.name());
        assertThat(response)
                .extracting(ContentResponse::releaseYear)
                .allSatisfy(year -> assertThat(year).isBetween(2025, 2026));
        assertThat(repository.lastMatchingCriteria)
                .isEqualTo(new SearchCriteria("architecture", Genre.DOCUMENTARY, 2025, 2026));
    }

    @Test
    void contentFilteringCanUsePhraseFromSearchAsAdditionalOptionalCriterion() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, new NoOpPublisher());
        ContentController controller = new ContentController(
                service,
                new NoRecommendations(),
                new FixedIdentityProvider(Role.VIEWER)
        );

        service.createMovie(new Metadata(
                "Clean Code",
                "Engineering documentary",
                "thumb-clean-code",
                Genre.DOCUMENTARY,
                2026,
                List.of(new Keyword("architecture"))
        ), Duration.ofSeconds(2400), new VideoFile("cdn://clean-code", List.of("pl")));
        service.createMovie(new Metadata(
                "Clean Comedy",
                "Comedy special",
                "thumb-clean-comedy",
                Genre.COMEDY,
                2026,
                List.of(new Keyword("architecture"))
        ), Duration.ofSeconds(2400), new VideoFile("cdn://clean-comedy", List.of("pl")));

        List<ContentResponse> response = controller.filter("Bearer jwt", "architecture", Genre.DOCUMENTARY, null, null);

        assertThat(response).singleElement()
                .extracting(ContentResponse::title)
                .isEqualTo("Clean Code");
    }

    @Test
    void contentFilteringRejectsInvalidYearRange() {
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(
                new InMemoryContentRepository(),
                new NoOpPublisher()
        );
        ContentController controller = new ContentController(
                service,
                new NoRecommendations(),
                new FixedIdentityProvider(Role.VIEWER)
        );

        assertThatThrownBy(() -> controller.filter("Bearer jwt", null, Genre.DOCUMENTARY, 2027, 2020))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void contentCreationRequiresLibraryAdminRole() {
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(
                new InMemoryContentRepository(),
                new NoOpPublisher()
        );
        ContentController controller = new ContentController(
                service,
                new NoRecommendations(),
                new FixedIdentityProvider(Role.VIEWER)
        );

        assertThatThrownBy(() -> controller.create("Bearer jwt", new ContentRequest(
                "Clean Architecture",
                "System test path",
                "thumb",
                Genre.DOCUMENTARY,
                2026,
                List.of("architecture"),
                2400,
                "cdn://clean-architecture",
                List.of("pl")
        )))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void contentCreationRejectsIncompleteMovieRequest() {
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(
                new InMemoryContentRepository(),
                new NoOpPublisher()
        );
        ContentController controller = new ContentController(
                service,
                new NoRecommendations(),
                new FixedIdentityProvider(Role.LIBRARY_ADMIN)
        );

        assertThatThrownBy(() -> controller.create("Bearer jwt", new ContentRequest(
                "Clean Architecture",
                "System test path",
                "thumb",
                Genre.DOCUMENTARY,
                2026,
                List.of("architecture"),
                2400,
                null,
                List.of("pl")
        )))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    static class InMemoryContentRepository implements ContentRepository {
        private final Map<ContentId, Content> contents = new LinkedHashMap<>();
        private String lastKeywordPhrase;
        private SearchCriteria lastMatchingCriteria;

        @Override
        public void save(Content content) {
            content.publish();
            contents.put(content.id(), content);
        }

        @Override
        public Optional<Content> ofId(ContentId id) {
            return Optional.ofNullable(contents.get(id));
        }

        @Override
        public List<Content> matching(SearchCriteria criteria) {
            lastMatchingCriteria = criteria;
            return new ArrayList<>(contents.values());
        }

        @Override
        public List<Content> byKeyword(String phrase) {
            lastKeywordPhrase = phrase;
            return new ArrayList<>(contents.values());
        }

        @Override
        public List<Content> page(Page page) {
            return new ArrayList<>(contents.values());
        }

        @Override
        public List<ContentId> popularContent(int limit) {
            return contents.keySet().stream().limit(limit).toList();
        }

        @Override
        public Optional<VideoFile> videoFileOf(ContentId id) {
            return Optional.ofNullable(contents.get(id))
                    .filter(Movie.class::isInstance)
                    .map(Movie.class::cast)
                    .map(Movie::videoFile);
        }

        @Override
        public Optional<ResolvedPlayable> resolvePlayable(UUID id) {
            return videoFileOf(new ContentId(id))
                    .map(videoFile -> new ResolvedPlayable(id, ResolvedPlayable.PlayableKind.MOVIE, videoFile));
        }

        @Override
        public boolean isSeries(ContentId id) {
            return Optional.ofNullable(contents.get(id))
                    .map(Series.class::isInstance)
                    .orElse(false);
        }

        @Override
        public void remove(ContentId id) {
            contents.remove(id);
        }
    }

    static class NoOpPublisher implements DomainEventPublisher {
        @Override
        public void publish(Object event) {
        }
    }

    static class RecordingPublisher implements DomainEventPublisher {
        private final List<Object> events = new ArrayList<>();

        @Override
        public void publish(Object event) {
            events.add(event);
        }
    }

    static class FixedIdentityProvider implements IdentityProvider {
        private final Role role;
        private final ViewerId viewerId = new ViewerId(UUID.randomUUID());

        FixedIdentityProvider(Role role) {
            this.role = role;
        }

        @Override
        public UserIdentity currentIdentity(String jwt) {
            return new UserIdentity(viewerId, role);
        }

        @Override
        public boolean verify(String jwt) {
            return true;
        }

        ViewerId viewerId() {
            return viewerId;
        }
    }

    static class NoRecommendations implements RecommendationUseCase {
        @Override
        public RecommendationList generateFor(com.project.youtlix.recommendation.domain.model.ViewerId viewerId) {
            return new RecommendationList(viewerId, Instant.now(), List.of());
        }

        @Override
        public void rate(
                com.project.youtlix.recommendation.domain.model.ViewerId viewerId,
                com.project.youtlix.recommendation.domain.model.ContentId contentId,
                StarRating stars
        ) {
        }

        @Override
        public void addToWatchlist(
                com.project.youtlix.recommendation.domain.model.ViewerId viewerId,
                com.project.youtlix.recommendation.domain.model.ContentId contentId
        ) {
        }

        @Override
        public void removeFromWatchlist(
                com.project.youtlix.recommendation.domain.model.ViewerId viewerId,
                com.project.youtlix.recommendation.domain.model.ContentId contentId
        ) {
        }
    }

    static class RecordingRecommendations extends NoRecommendations {
        private UUID requestedViewerId;

        @Override
        public RecommendationList generateFor(com.project.youtlix.recommendation.domain.model.ViewerId viewerId) {
            requestedViewerId = viewerId.value();
            return new RecommendationList(
                    viewerId,
                    Instant.now(),
                    List.of(new RecommendedItem(
                            new com.project.youtlix.recommendation.domain.model.ContentId(UUID.randomUUID()),
                            0.8,
                            RecommendationReason.GLOBAL_POPULARITY
                    ))
            );
        }
    }
}
