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
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Movie;
import com.project.youtlix.contentlibrary.domain.model.Page;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;
import com.project.youtlix.contentlibrary.domain.model.Series;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentController;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentRequest;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentResponse;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.StarRating;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ContentLibrarySystemTest {

    @Test
    void contentCreationPathRunsFromWebAdapterToRepositoryPort() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, new NoOpPublisher());
        ContentController controller = new ContentController(
                service,
                new NoRecommendations(),
                new FixedIdentityProvider(Role.LIBRARY_ADMIN)
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

        List<ContentResponse> response = controller.browse("Bearer jwt", 1, 20).contents();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().title()).isEqualTo("Clean Architecture");
    }

    static class InMemoryContentRepository implements ContentRepository {
        private final Map<ContentId, Content> contents = new LinkedHashMap<>();

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
            return new ArrayList<>(contents.values());
        }

        @Override
        public List<Content> byKeyword(String phrase) {
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
}
