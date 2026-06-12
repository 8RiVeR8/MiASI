package com.project.youtlix.contentlibrary.application.service;

import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import com.project.youtlix.contentlibrary.application.port.in.ContentMetadata;
import com.project.youtlix.contentlibrary.application.port.out.ContentRepository;
import com.project.youtlix.contentlibrary.domain.model.Content;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Duration;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Movie;
import com.project.youtlix.contentlibrary.domain.model.Page;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.contentlibrary.domain.service.ContentSearchService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ContentLibraryUseCaseTest {

    @Test
    void createMovieMakesItBrowsableAndExposesCatalogMetadata() {
        InMemoryContentRepository repository = new InMemoryContentRepository();
        RecordingPublisher publisher = new RecordingPublisher();
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(repository, publisher);
        Metadata metadata = new Metadata(
                "DDD Movie",
                "Architecture",
                "thumb",
                Genre.DOCUMENTARY,
                2026,
                List.of(new Keyword("architecture"))
        );

        ContentId id = service.createMovie(metadata, Duration.ofSeconds(3600), new VideoFile("cdn://ddd", List.of("en")));
        ContentMetadata catalogMetadata = service.metadataOf(id);

        assertThat(service.browse(new Page(0, 10))).hasSize(1);
        assertThat(catalogMetadata.title()).isEqualTo("DDD Movie");
        assertThat(service.videoFileOf(id).uri()).isEqualTo("cdn://ddd");
        assertThat(publisher.events).isNotEmpty();
    }

    static class InMemoryContentRepository implements ContentRepository {
        private final Map<ContentId, Content> contents = new LinkedHashMap<>();
        private final ContentSearchService searchService = new ContentSearchService();

        @Override
        public void save(Content content) {
            contents.put(content.id(), content);
        }

        @Override
        public Optional<Content> ofId(ContentId id) {
            return Optional.ofNullable(contents.get(id));
        }

        @Override
        public List<Content> matching(SearchCriteria criteria) {
            return searchService.filter(new ArrayList<>(contents.values()), criteria);
        }

        @Override
        public List<Content> byKeyword(String phrase) {
            return matching(new SearchCriteria(phrase, null, null, null));
        }

        @Override
        public List<Content> page(Page page) {
            return contents.values().stream()
                    .skip((long) page.number() * page.size())
                    .limit(page.size())
                    .toList();
        }

        @Override
        public List<ContentId> popularContent(int limit) {
            return contents.values().stream()
                    .sorted(Comparator.comparing(content -> content.metadata().releaseYear(), Comparator.reverseOrder()))
                    .limit(limit)
                    .map(Content::id)
                    .toList();
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

    static class RecordingPublisher implements DomainEventPublisher {
        private final List<Object> events = new ArrayList<>();

        @Override
        public void publish(Object event) {
            events.add(event);
        }
    }
}
