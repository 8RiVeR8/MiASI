package com.project.youtlix.system;

import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import com.project.youtlix.common.domain.model.DomainEvent;
import com.project.youtlix.contentlibrary.application.port.out.ContentRepository;
import com.project.youtlix.contentlibrary.application.service.ContentLibraryService;
import com.project.youtlix.contentlibrary.domain.model.Content;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Movie;
import com.project.youtlix.contentlibrary.domain.model.Page;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentController;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentRequest;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentResponse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ContentLibrarySystemTest {

    @Test
    void contentCreationPathRunsFromWebAdapterToRepositoryPort() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        ContentLibraryService service = new ContentLibraryService(repository, new NoOpPublisher());
        ContentController controller = new ContentController(service);

        controller.createMovie(new ContentRequest(
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

        List<ContentResponse> response = controller.browse(0, 20);

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
        public void remove(ContentId id) {
            contents.remove(id);
        }
    }

    static class NoOpPublisher implements DomainEventPublisher {
        @Override
        public void publish(DomainEvent event) {
        }
    }
}
