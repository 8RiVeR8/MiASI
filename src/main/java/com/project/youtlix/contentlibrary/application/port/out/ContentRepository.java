package com.project.youtlix.contentlibrary.application.port.out;

import com.project.youtlix.contentlibrary.application.port.in.ResolvedPlayable;
import com.project.youtlix.contentlibrary.domain.model.Content;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Page;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for content aggregates.
 */
public interface ContentRepository {

    /** Persists a content aggregate. */
    void save(Content content);

    /** Loads content by id. */
    Optional<Content> ofId(ContentId id);

    /** Finds content matching criteria. */
    List<Content> matching(SearchCriteria criteria);

    /** Finds content by keyword phrase. */
    List<Content> byKeyword(String phrase);

    /** Returns a page of content. */
    List<Content> page(Page page);

    /** Returns popular content identifiers. */
    List<ContentId> popularContent(int limit);

    /** Returns video file data for a content id. */
    Optional<VideoFile> videoFileOf(ContentId id);

    /** Resolves a movie or episode id to its video file. */
    Optional<ResolvedPlayable> resolvePlayable(UUID id);

    /** Checks whether content id refers to a series container. */
    boolean isSeries(ContentId id);

    /** Removes content by id. */
    void remove(ContentId id);
}
