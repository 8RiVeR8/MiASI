package com.project.youtlix.contentlibrary.infrastructure.out.persistence;

import com.project.youtlix.contentlibrary.application.port.out.ContentRepository;
import com.project.youtlix.contentlibrary.domain.model.Content;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Movie;
import com.project.youtlix.contentlibrary.domain.model.Page;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Driven adapter for Supabase Postgres library schema.
 *
 * <p>The database schema is already prepared in resources/database. This adapter
 * defines the boundary and contains the first row mapping for library.contents;
 * full aggregate reconstruction for movies, series, seasons and episodes should
 * be added while implementing concrete features.</p>
 */
@Repository
public class SupabaseContentRepository implements ContentRepository {

    private final SpringDataContentJpaRepository jpaRepository;

    /**
     * Creates a persistence adapter using Spring Data JPA.
     */
    public SupabaseContentRepository(SpringDataContentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Content content) {
        Metadata metadata = content.metadata();
        ContentType type = content instanceof Movie ? ContentType.MOVIE : ContentType.SERIES;
        jpaRepository.save(new ContentJpaEntity(
                content.id().value(),
                type,
                metadata.title(),
                metadata.description(),
                metadata.thumbnailUrl(),
                metadata.genre(),
                metadata.releaseYear(),
                content.available()
        ));
    }

    @Override
    public Optional<Content> ofId(ContentId id) {
        return Optional.empty();
    }

    @Override
    public List<Content> matching(SearchCriteria criteria) {
        return List.of();
    }

    @Override
    public List<Content> byKeyword(String phrase) {
        return List.of();
    }

    @Override
    public List<Content> page(Page page) {
        return List.of();
    }

    @Override
    public List<ContentId> popularContent(int limit) {
        return jpaRepository.findByAvailableTrueOrderByReleaseYearDesc().stream()
                .limit(limit)
                .map(row -> new ContentId(row.id()))
                .toList();
    }

    @Override
    public Optional<VideoFile> videoFileOf(ContentId id) {
        return Optional.empty();
    }

    @Override
    public void remove(ContentId id) {
        jpaRepository.deleteById(id.value());
    }
}
