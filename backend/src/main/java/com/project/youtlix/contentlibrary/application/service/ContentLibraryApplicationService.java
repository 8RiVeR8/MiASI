package com.project.youtlix.contentlibrary.application.service;

import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import com.project.youtlix.common.domain.DomainException;
import com.project.youtlix.contentlibrary.application.port.in.*;
import com.project.youtlix.contentlibrary.application.port.out.ContentRepository;
import com.project.youtlix.contentlibrary.domain.model.*;
import com.project.youtlix.contentlibrary.domain.service.ContentFactory;
import com.project.youtlix.contentlibrary.domain.service.ContentSearchService;
import org.springframework.stereotype.Service;
import java.util.List;

/** Application service implementing content library use cases PU5-PU10. */
@Service
public class ContentLibraryApplicationService implements ContentLibraryUseCase, ContentCatalogApi {
    private final ContentRepository repository;
    private final ContentFactory factory;
    private final ContentSearchService searchService;
    private final DomainEventPublisher eventPublisher;

    public ContentLibraryApplicationService(ContentRepository repository, ContentFactory factory,
            ContentSearchService searchService, DomainEventPublisher eventPublisher) {
        this.repository = repository;
        this.factory = factory;
        this.searchService = searchService;
        this.eventPublisher = eventPublisher;
    }

    @Override public List<Content> browse(Page page) { return searchService.browse(page, repository.page(page)); }
    @Override public List<Content> searchByKeyword(String phrase) { return searchService.searchByKeyword(phrase, repository.byKeyword(phrase)); }
    @Override public List<Content> filter(SearchCriteria criteria) { return searchService.filter(criteria, repository.matching(criteria)); }

    @Override public ContentId createMovie(CreateMovieCommand command) {
        Movie movie = factory.createMovie(command.metadata(), command.duration(), command.videoFile());
        repository.save(movie);
        eventPublisher.publishAll(movie.occurredEvents());
        return movie.id();
    }

    @Override public void updateMetadata(UpdateMetadataCommand command) {
        Content content = repository.ofId(command.contentId()).orElseThrow(() -> new DomainException("Content not found"));
        content.updateMetadata(command.metadata());
        repository.save(content);
        eventPublisher.publishAll(content.occurredEvents());
    }

    @Override public void removeContent(ContentId contentId) {
        Content content = repository.ofId(contentId).orElseThrow(() -> new DomainException("Content not found"));
        content.withdraw();
        repository.remove(contentId);
        eventPublisher.publishAll(content.occurredEvents());
    }

    @Override public VideoFile videoFileOf(ContentId id) {
        Content content = repository.ofId(id).orElseThrow(() -> new DomainException("Content not found"));
        if (content instanceof Movie movie) return movie.videoFile();
        throw new DomainException("Series playback file must be selected by episode");
    }

    @Override public List<ContentId> popularContent(int limit) {
        return repository.page(new Page(0, Math.max(limit, 1))).stream().map(Content::id).toList();
    }

    @Override public ContentMetadata metadataOf(ContentId id) {
        Metadata metadata = repository.ofId(id).orElseThrow(() -> new DomainException("Content not found")).metadata();
        return new ContentMetadata(metadata.title(), metadata.genre(), metadata.keywords(), metadata.releaseYear());
    }
}
