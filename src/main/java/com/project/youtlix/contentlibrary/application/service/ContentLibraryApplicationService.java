package com.project.youtlix.contentlibrary.application.service;

import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import com.project.youtlix.contentlibrary.application.port.in.ContentCatalogApi;
import com.project.youtlix.contentlibrary.application.port.in.ContentLibraryUseCase;
import com.project.youtlix.contentlibrary.application.port.in.ContentMetadata;
import com.project.youtlix.contentlibrary.application.port.in.ContentNotFoundException;
import com.project.youtlix.contentlibrary.application.port.in.PlayableNotFoundException;
import com.project.youtlix.contentlibrary.application.port.in.ResolvedPlayable;
import com.project.youtlix.contentlibrary.application.port.in.SeriesNotPlayableException;
import com.project.youtlix.contentlibrary.application.port.out.ContentRepository;
import com.project.youtlix.contentlibrary.domain.model.Content;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Duration;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Movie;
import com.project.youtlix.contentlibrary.domain.model.Page;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;
import com.project.youtlix.contentlibrary.domain.model.Series;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.contentlibrary.domain.service.ContentFactory;
import com.project.youtlix.contentlibrary.domain.service.ContentSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service coordinating library use cases and catalog OHS queries.
 */
@Service
public class ContentLibraryApplicationService implements ContentLibraryUseCase, ContentCatalogApi {

    private final ContentRepository contentRepository;
    private final DomainEventPublisher eventPublisher;
    private final ContentFactory contentFactory;
    private final ContentSearchService contentSearchService;

    /**
     * Creates the content library application service.
     */
    @Autowired
    public ContentLibraryApplicationService(ContentRepository contentRepository, DomainEventPublisher eventPublisher) {
        this(contentRepository, eventPublisher, new ContentFactory(), new ContentSearchService());
    }

    /**
     * Constructor useful for tests with an explicit factory.
     */
    public ContentLibraryApplicationService(
            ContentRepository contentRepository,
            DomainEventPublisher eventPublisher,
            ContentFactory contentFactory,
            ContentSearchService contentSearchService
    ) {
        this.contentRepository = contentRepository;
        this.eventPublisher = eventPublisher;
        this.contentFactory = contentFactory;
        this.contentSearchService = contentSearchService;
    }

    @Override
    public List<Content> browse(Page page) {
        return contentSearchService.browse(contentRepository.page(page));
    }

    @Override
    public List<Content> searchByKeyword(String phrase) {
        return contentSearchService.searchByKeyword(contentRepository.byKeyword(phrase), phrase);
    }

    @Override
    public List<Content> filter(SearchCriteria criteria) {
        return contentSearchService.filter(contentRepository.matching(criteria), criteria);
    }

    @Override
    public ContentId createMovie(Metadata metadata, Duration duration, VideoFile videoFile) {
        Movie movie = contentFactory.createMovie(metadata, duration, videoFile);
        contentRepository.save(movie);
        eventPublisher.publishAll(movie.occurredEvents());
        return movie.id();
    }

    @Override
    public ContentId createSeries(Metadata metadata) {
        Series series = contentFactory.createSeries(metadata);
        contentRepository.save(series);
        eventPublisher.publishAll(series.occurredEvents());
        return series.id();
    }

    @Override
    public void updateMetadata(ContentId id, Metadata metadata) {
        Content content = contentRepository.ofId(id)
                .orElseThrow(() -> new ContentNotFoundException(id.value()));
        content.updateMetadata(metadata);
        contentRepository.save(content);
        eventPublisher.publishAll(content.occurredEvents());
    }

    @Override
    public void remove(ContentId id) {
        contentRepository.ofId(id).ifPresent(content -> {
            content.withdraw();
            eventPublisher.publishAll(content.occurredEvents());
        });
        contentRepository.remove(id);
    }

    @Override
    public List<ContentId> popularContent(int limit) {
        return contentRepository.popularContent(limit);
    }

    @Override
    public ContentMetadata metadataOf(ContentId id) {
        Content content = contentRepository.ofId(id)
                .orElseThrow(() -> new IllegalArgumentException("content not found: " + id.value()));
        Metadata metadata = content.metadata();
        return new ContentMetadata(metadata.genre(), metadata.keywords(), metadata.releaseYear(), metadata.title());
    }

    @Override
    public VideoFile videoFileOf(ContentId id) {
        return contentRepository.videoFileOf(id)
                .orElseThrow(() -> new IllegalArgumentException("video file not found for content: " + id.value()));
    }

    @Override
    public ResolvedPlayable resolvePlayable(java.util.UUID id) {
        return contentRepository.resolvePlayable(id)
                .orElseThrow(() -> {
                    if (contentRepository.isSeries(new ContentId(id))) {
                        throw new SeriesNotPlayableException(id);
                    }
                    throw new PlayableNotFoundException(id);
                });
    }
}
