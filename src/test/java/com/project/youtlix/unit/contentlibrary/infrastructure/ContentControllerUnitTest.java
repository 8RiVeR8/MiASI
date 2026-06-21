package com.project.youtlix.unit.contentlibrary.infrastructure;

import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.NoOpDomainEventPublisher;
import com.project.youtlix.testsupport.fixture.RecordingDomainEventPublisher;
import com.project.youtlix.testsupport.fixture.RoleIdentityProvider;
import com.project.youtlix.testsupport.fixture.memory.InMemoryContentRepository;
import com.project.youtlix.testsupport.fixture.stub.NoOpRecommendationUseCase;
import com.project.youtlix.testsupport.fixture.stub.RecordingRecommendationUseCase;

import com.project.youtlix.authentication.domain.model.Role;
import com.project.youtlix.authentication.domain.model.UserIdentity;
import com.project.youtlix.contentlibrary.application.port.in.PlayableNotFoundException;
import com.project.youtlix.contentlibrary.application.port.in.ResolvedPlayable;
import com.project.youtlix.contentlibrary.application.port.in.SeriesContentExpectedException;
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
import com.project.youtlix.contentlibrary.domain.model.event.ContentModified;
import com.project.youtlix.contentlibrary.domain.model.event.ContentRemoved;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentController;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentRequest;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentResponse;
import com.project.youtlix.contentlibrary.domain.model.ContentType;
import com.project.youtlix.contentlibrary.infrastructure.in.web.EpisodeRequest;
import com.project.youtlix.contentlibrary.infrastructure.in.web.SeasonRequest;
import com.project.youtlix.recommendation.domain.model.RecommendationReason;
import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.RecommendedItem;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UnitTest
class ContentControllerUnitTest {

    @Test
    void contentCreationPathRunsFromWebAdapterToRepositoryPort() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        RecordingDomainEventPublisher publisher = new RecordingDomainEventPublisher();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, publisher);
        RoleIdentityProvider identityProvider = new RoleIdentityProvider(Role.LIBRARY_ADMIN);
        RecordingRecommendationUseCase recommendations = new RecordingRecommendationUseCase();
        ContentController controller = new ContentController(
                service,
                recommendations,
                identityProvider
        );

        controller.create("Bearer jwt", new ContentRequest(
                ContentType.MOVIE,
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

        List<ContentResponse> contents = controller.browse("Bearer jwt", 1, 20);

        assertThat(contents).hasSize(1);
        assertThat(contents.getFirst().type()).isEqualTo("MOVIE");
        assertThat(contents.getFirst().title()).isEqualTo("Clean Architecture");
        assertThat(contents.getFirst().thumbnailUrl()).isEqualTo("thumb");
        assertThat(contents.getFirst().durationSeconds()).isEqualTo(2400);
        assertThat(contents.getFirst().videoUri()).isEqualTo("cdn://clean-architecture");
        assertThat(contents.getFirst().languages()).containsExactly("pl");
        assertThat(contents.getFirst().seasons()).isEmpty();
        assertThat(publisher.events())
                .anySatisfy(event -> assertThat(event)
                        .isInstanceOfSatisfying(ContentAdded.class, added ->
                                assertThat(added.title()).isEqualTo("Clean Architecture")));
    }

    @Test
    void contentSearchPathRunsFromWebAdapterThroughUseCaseAndApplicationService() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, new NoOpDomainEventPublisher());
        ContentController controller = new ContentController(
                service,
                new NoOpRecommendationUseCase(),
                new RoleIdentityProvider(Role.VIEWER)
        );

        service.createMovie(new Metadata(
                "Clean Architecture",
                "Hexagonal architecture documentary",
                ContentType.MOVIE, "thumb-clean",
                Genre.DOCUMENTARY,
                2026,
                List.of(new Keyword("Architecture"))
        ), Duration.ofSeconds(2400), new VideoFile("cdn://clean-architecture", List.of("pl")));
        service.createMovie(new Metadata(
                "Comedy Night",
                "Stand-up special",
                ContentType.MOVIE, "thumb-comedy",
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
                new NoOpDomainEventPublisher()
        );
        ContentController controller = new ContentController(
                service,
                new NoOpRecommendationUseCase(),
                new RoleIdentityProvider(Role.VIEWER)
        );

        assertThatThrownBy(() -> controller.search("Bearer jwt", " "))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void contentFilteringPathRunsFromWebAdapterThroughUseCaseAndApplicationService() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, new NoOpDomainEventPublisher());
        ContentController controller = new ContentController(
                service,
                new NoOpRecommendationUseCase(),
                new RoleIdentityProvider(Role.VIEWER)
        );

        service.createMovie(new Metadata(
                "Clean Architecture",
                "Hexagonal architecture documentary",
                ContentType.MOVIE, "thumb-clean",
                Genre.DOCUMENTARY,
                2026,
                List.of(new Keyword("architecture"))
        ), Duration.ofSeconds(2400), new VideoFile("cdn://clean-architecture", List.of("pl")));
        service.createMovie(new Metadata(
                "Comedy Night",
                "Stand-up special",
                ContentType.MOVIE, "thumb-comedy",
                Genre.COMEDY,
                2024,
                List.of(new Keyword("standup"))
        ), Duration.ofSeconds(3600), new VideoFile("cdn://comedy-night", List.of("pl")));
        service.createMovie(new Metadata(
                "Old Documentary",
                "Older documentary",
                ContentType.MOVIE, "thumb-old",
                Genre.DOCUMENTARY,
                2020,
                List.of(new Keyword("archive"))
        ), Duration.ofSeconds(1800), new VideoFile("cdn://old-documentary", List.of("pl")));
        service.createMovie(new Metadata(
                "Architecture Clean",
                "Same words but title does not start with filter phrase",
                ContentType.MOVIE, "thumb-architecture-clean",
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
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, new NoOpDomainEventPublisher());
        ContentController controller = new ContentController(
                service,
                new NoOpRecommendationUseCase(),
                new RoleIdentityProvider(Role.VIEWER)
        );

        service.createMovie(new Metadata(
                "Clean Code",
                "Engineering documentary",
                ContentType.MOVIE, "thumb-clean-code",
                Genre.DOCUMENTARY,
                2026,
                List.of(new Keyword("architecture"))
        ), Duration.ofSeconds(2400), new VideoFile("cdn://clean-code", List.of("pl")));
        service.createMovie(new Metadata(
                "Clean Comedy",
                "Comedy special",
                ContentType.MOVIE, "thumb-clean-comedy",
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
                new NoOpDomainEventPublisher()
        );
        ContentController controller = new ContentController(
                service,
                new NoOpRecommendationUseCase(),
                new RoleIdentityProvider(Role.VIEWER)
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
                new NoOpDomainEventPublisher()
        );
        ContentController controller = new ContentController(
                service,
                new NoOpRecommendationUseCase(),
                new RoleIdentityProvider(Role.VIEWER)
        );

        assertThatThrownBy(() -> controller.create("Bearer jwt", new ContentRequest(
                ContentType.MOVIE,
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
                new NoOpDomainEventPublisher()
        );
        ContentController controller = new ContentController(
                service,
                new NoOpRecommendationUseCase(),
                new RoleIdentityProvider(Role.LIBRARY_ADMIN)
        );

        assertThatThrownBy(() -> controller.create("Bearer jwt", new ContentRequest(
                ContentType.MOVIE,
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

    @Test
    void seriesCreationCanBeExtendedWithSeasonAndEpisode() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, new NoOpDomainEventPublisher());
        ContentController controller = new ContentController(
                service,
                new NoOpRecommendationUseCase(),
                new RoleIdentityProvider(Role.LIBRARY_ADMIN)
        );
        UUID seriesId = controller.create("Bearer jwt", new ContentRequest(
                ContentType.SERIES,
                "Breaking Bad",
                "Crime drama series",
                "thumb-breaking-bad",
                Genre.DRAMA,
                2008,
                List.of("crime", "drama"),
                null,
                null,
                List.of()
        ));

        UUID seasonId = controller.addSeason("Bearer jwt", seriesId, new SeasonRequest(1, "Season 1"));
        UUID episodeId = controller.addEpisode("Bearer jwt", seriesId, seasonId, new EpisodeRequest(
                1,
                "Pilot",
                3480,
                "cdn://breaking-bad/s01e01",
                List.of("pl", "en")
        ));

        Content stored = repository.ofId(new ContentId(seriesId)).orElseThrow();
        ResolvedPlayable playable = service.resolvePlayable(episodeId);
        ContentResponse response = controller.browse("Bearer jwt", 1, 20).getFirst();

        assertThat(stored).isInstanceOfSatisfying(Series.class, series ->
                assertThat(series.seasons()).singleElement()
                        .satisfies(season -> {
                            assertThat(season.id().value()).isEqualTo(seasonId);
                            assertThat(season.number()).isEqualTo(1);
                            assertThat(season.title()).isEqualTo("Season 1");
                            assertThat(season.episodes()).singleElement()
                                    .satisfies(episode -> {
                                        assertThat(episode.id().value()).isEqualTo(episodeId);
                                        assertThat(episode.title()).isEqualTo("Pilot");
                                    });
                        }));
        assertThat(response.type()).isEqualTo(ContentType.SERIES.name());
        assertThat(response.durationSeconds()).isNull();
        assertThat(response.videoUri()).isNull();
        assertThat(response.languages()).isEmpty();
        assertThat(response.seasons()).singleElement()
                .satisfies(season -> {
                    assertThat(season.id()).isEqualTo(seasonId);
                    assertThat(season.number()).isEqualTo(1);
                    assertThat(season.title()).isEqualTo("Season 1");
                    assertThat(season.episodes()).singleElement()
                            .satisfies(episode -> {
                                assertThat(episode.id()).isEqualTo(episodeId);
                                assertThat(episode.number()).isEqualTo(1);
                                assertThat(episode.title()).isEqualTo("Pilot");
                                assertThat(episode.durationSeconds()).isEqualTo(3480);
                                assertThat(episode.videoUri()).isEqualTo("cdn://breaking-bad/s01e01");
                                assertThat(episode.languages()).containsExactly("pl", "en");
                            });
                });
        assertThat(playable.kind()).isEqualTo(ResolvedPlayable.PlayableKind.EPISODE);
        assertThat(playable.videoFile().uri()).isEqualTo("cdn://breaking-bad/s01e01");
    }

    @Test
    void seriesSeasonAndEpisodeCanBeUpdatedSeparately() {
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
                "Breaking Bad",
                "Crime drama series",
                "thumb-breaking-bad",
                Genre.DRAMA,
                2008,
                List.of("crime", "drama"),
                null,
                null,
                List.of()
        ));
        UUID seasonId = controller.addSeason("Bearer jwt", seriesId, new SeasonRequest(1, "Season 1"));
        UUID episodeId = controller.addEpisode("Bearer jwt", seriesId, seasonId, new EpisodeRequest(
                1,
                "Pilot",
                3480,
                "cdn://breaking-bad/s01e01",
                List.of("pl", "en")
        ));
        publisher.clear();

        controller.updateSeason("Bearer jwt", seriesId, seasonId, new SeasonRequest(2, "Updated Season"));
        controller.updateEpisode("Bearer jwt", seriesId, seasonId, episodeId, new EpisodeRequest(
                2,
                "Updated Pilot",
                3600,
                "cdn://breaking-bad/updated-pilot",
                List.of("en")
        ));

        ContentResponse response = controller.browse("Bearer jwt", 1, 20).getFirst();
        ResolvedPlayable playable = service.resolvePlayable(episodeId);

        assertThat(response.seasons()).singleElement()
                .satisfies(season -> {
                    assertThat(season.id()).isEqualTo(seasonId);
                    assertThat(season.number()).isEqualTo(2);
                    assertThat(season.title()).isEqualTo("Updated Season");
                    assertThat(season.episodes()).singleElement()
                            .satisfies(episode -> {
                                assertThat(episode.id()).isEqualTo(episodeId);
                                assertThat(episode.number()).isEqualTo(2);
                                assertThat(episode.title()).isEqualTo("Updated Pilot");
                                assertThat(episode.durationSeconds()).isEqualTo(3600);
                                assertThat(episode.videoUri()).isEqualTo("cdn://breaking-bad/updated-pilot");
                                assertThat(episode.languages()).containsExactly("en");
                            });
                });
        assertThat(playable.videoFile().uri()).isEqualTo("cdn://breaking-bad/updated-pilot");
        assertThat(publisher.events())
                .anySatisfy(event -> assertThat(event)
                        .isInstanceOfSatisfying(ContentModified.class, modified ->
                                assertThat(modified.contentId().value()).isEqualTo(seriesId)));
    }

    @Test
    void seasonCannotBeAddedToMovieContent() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, new NoOpDomainEventPublisher());
        ContentController controller = new ContentController(
                service,
                new NoOpRecommendationUseCase(),
                new RoleIdentityProvider(Role.LIBRARY_ADMIN)
        );
        UUID movieId = controller.create("Bearer jwt", new ContentRequest(
                ContentType.MOVIE,
                "John Wick",
                "Original description",
                "original-thumb",
                Genre.ACTION,
                2014,
                List.of("action"),
                6060,
                "https://www.youtube.com/watch?v=C0BMx-qxsP4",
                List.of("pl")
        ));

        assertThat(controller.browse("Bearer jwt", 1, 20)).singleElement()
                .extracting(ContentResponse::type)
                .isEqualTo(ContentType.MOVIE.name());
        assertThatThrownBy(() -> controller.addSeason("Bearer jwt", movieId, new SeasonRequest(1, "Season 1")))
                .isInstanceOf(SeriesContentExpectedException.class);
    }

    @Test
    void seriesMetadataUpdatePreservesSeasonsAndEpisodes() {
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
                "Breaking Bad",
                "Crime drama series",
                "thumb-breaking-bad",
                Genre.DRAMA,
                2008,
                List.of("crime", "drama"),
                null,
                null,
                List.of()
        ));
        UUID seasonId = controller.addSeason("Bearer jwt", seriesId, new SeasonRequest(1, "Season 1"));
        UUID episodeId = controller.addEpisode("Bearer jwt", seriesId, seasonId, new EpisodeRequest(
                1,
                "Pilot",
                3480,
                "cdn://breaking-bad/s01e01",
                List.of("pl", "en")
        ));
        publisher.clear();

        controller.update("Bearer jwt", seriesId, new ContentRequest(
                ContentType.SERIES,
                "Breaking Bad: Updated",
                "Updated crime drama series",
                "thumb-breaking-bad-updated",
                Genre.DRAMA,
                2008,
                List.of("crime", "meth", "drama"),
                null,
                null,
                List.of()
        ));

        ContentResponse response = controller.browse("Bearer jwt", 1, 20).getFirst();

        assertThat(response.id()).isEqualTo(seriesId);
        assertThat(response.type()).isEqualTo(ContentType.SERIES.name());
        assertThat(response.title()).isEqualTo("Breaking Bad: Updated");
        assertThat(response.description()).isEqualTo("Updated crime drama series");
        assertThat(response.thumbnailUrl()).isEqualTo("thumb-breaking-bad-updated");
        assertThat(response.seasons()).singleElement()
                .satisfies(season -> {
                    assertThat(season.id()).isEqualTo(seasonId);
                    assertThat(season.episodes()).singleElement()
                            .satisfies(episode -> {
                                assertThat(episode.id()).isEqualTo(episodeId);
                                assertThat(episode.videoUri()).isEqualTo("cdn://breaking-bad/s01e01");
                            });
                });
        assertThat(publisher.events())
                .anySatisfy(event -> assertThat(event)
                        .isInstanceOfSatisfying(ContentModified.class, modified ->
                                assertThat(modified.contentId().value()).isEqualTo(seriesId)));
    }

    @Test
    void contentMetadataUpdatePathRunsFromWebAdapterThroughUseCaseAndPublishesEvent() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        RecordingDomainEventPublisher publisher = new RecordingDomainEventPublisher();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, publisher);
        ContentController controller = new ContentController(
                service,
                new NoOpRecommendationUseCase(),
                new RoleIdentityProvider(Role.LIBRARY_ADMIN)
        );
        UUID contentId = controller.create("Bearer jwt", new ContentRequest(
                ContentType.MOVIE,
                "John Wick",
                "Original description",
                "original-thumb",
                Genre.ACTION,
                2014,
                List.of("action"),
                6060,
                "https://www.youtube.com/watch?v=C0BMx-qxsP4",
                List.of("pl")
        ));
        publisher.clear();

        controller.update("Bearer jwt", contentId, new ContentRequest(
                ContentType.MOVIE,
                "John Wick: Chapter 1",
                "Updated description",
                "updated-thumb",
                Genre.ACTION,
                2014,
                List.of("action", "revenge"),
                6100,
                "https://www.youtube.com/watch?v=updated-john-wick",
                List.of("pl", "en")
        ));

        List<ContentResponse> response = controller.browse("Bearer jwt", 1, 20);

        assertThat(response).singleElement()
                .satisfies(content -> {
                    assertThat(content.id()).isEqualTo(contentId);
                    assertThat(content.title()).isEqualTo("John Wick: Chapter 1");
                    assertThat(content.description()).isEqualTo("Updated description");
                    assertThat(content.thumbnailUrl()).isEqualTo("updated-thumb");
                    assertThat(content.durationSeconds()).isEqualTo(6100);
                    assertThat(content.videoUri()).isEqualTo("https://www.youtube.com/watch?v=updated-john-wick");
                    assertThat(content.languages()).containsExactly("pl", "en");
                });
        assertThat(publisher.events())
                .anySatisfy(event -> assertThat(event)
                        .isInstanceOfSatisfying(ContentModified.class, modified ->
                                assertThat(modified.contentId().value()).isEqualTo(contentId)));
    }

    @Test
    void contentMetadataUpdateRequiresLibraryAdminRole() {
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(
                new InMemoryContentRepository(),
                new NoOpDomainEventPublisher()
        );
        ContentController controller = new ContentController(
                service,
                new NoOpRecommendationUseCase(),
                new RoleIdentityProvider(Role.VIEWER)
        );

        assertThatThrownBy(() -> controller.update("Bearer jwt", UUID.randomUUID(), new ContentRequest(
                ContentType.MOVIE,
                "John Wick",
                "Description",
                "thumb",
                Genre.ACTION,
                2014,
                List.of("action"),
                6060,
                "https://www.youtube.com/watch?v=C0BMx-qxsP4",
                List.of("pl")
        )))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void contentRemovalPathRunsFromWebAdapterThroughUseCaseAndPublishesEvent() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        RecordingDomainEventPublisher publisher = new RecordingDomainEventPublisher();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, publisher);
        ContentController controller = new ContentController(
                service,
                new NoOpRecommendationUseCase(),
                new RoleIdentityProvider(Role.LIBRARY_ADMIN)
        );
        UUID contentId = controller.create("Bearer jwt", new ContentRequest(
                ContentType.MOVIE,
                "John Wick",
                "Original description",
                "original-thumb",
                Genre.ACTION,
                2014,
                List.of("action"),
                6060,
                "https://www.youtube.com/watch?v=C0BMx-qxsP4",
                List.of("pl")
        ));
        publisher.clear();

        controller.delete("Bearer jwt", contentId);

        assertThat(controller.browse("Bearer jwt", 1, 20)).isEmpty();
        assertThat(publisher.events())
                .anySatisfy(event -> assertThat(event)
                        .isInstanceOfSatisfying(ContentRemoved.class, removed ->
                                assertThat(removed.contentId().value()).isEqualTo(contentId)));
    }

    @Test
    void seriesRemovalRemovesItsSeasonsAndEpisodesFromCatalog() {
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
                "Breaking Bad",
                "Crime drama series",
                "thumb-breaking-bad",
                Genre.DRAMA,
                2008,
                List.of("crime", "drama"),
                null,
                null,
                List.of()
        ));
        UUID seasonId = controller.addSeason("Bearer jwt", seriesId, new SeasonRequest(1, "Season 1"));
        UUID episodeId = controller.addEpisode("Bearer jwt", seriesId, seasonId, new EpisodeRequest(
                1,
                "Pilot",
                3480,
                "cdn://breaking-bad/s01e01",
                List.of("pl", "en")
        ));
        assertThat(service.resolvePlayable(episodeId).kind()).isEqualTo(ResolvedPlayable.PlayableKind.EPISODE);
        publisher.clear();

        controller.delete("Bearer jwt", seriesId);

        assertThat(controller.browse("Bearer jwt", 1, 20)).isEmpty();
        assertThatThrownBy(() -> service.resolvePlayable(episodeId))
                .isInstanceOf(PlayableNotFoundException.class);
        assertThat(publisher.events())
                .anySatisfy(event -> assertThat(event)
                        .isInstanceOfSatisfying(ContentRemoved.class, removed ->
                                assertThat(removed.contentId().value()).isEqualTo(seriesId)));
    }

    @Test
    void contentRemovalRequiresLibraryAdminRole() {
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(
                new InMemoryContentRepository(),
                new NoOpDomainEventPublisher()
        );
        ContentController controller = new ContentController(
                service,
                new NoOpRecommendationUseCase(),
                new RoleIdentityProvider(Role.VIEWER)
        );

        assertThatThrownBy(() -> controller.delete("Bearer jwt", UUID.randomUUID()))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }
}
