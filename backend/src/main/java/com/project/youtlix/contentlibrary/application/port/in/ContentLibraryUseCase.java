package com.project.youtlix.contentlibrary.application.port.in;

import com.project.youtlix.contentlibrary.domain.model.*;
import java.util.List;

/** Inbound port exposing PU5-PU10 content library use cases. */
public interface ContentLibraryUseCase {
    /** Browses available content with pagination. */
    List<Content> browse(Page page);
    /** Searches content by a keyword phrase. */
    List<Content> searchByKeyword(String phrase);
    /** Filters content using selected criteria. */
    List<Content> filter(SearchCriteria criteria);
    /** Creates a movie in the library. */
    ContentId createMovie(CreateMovieCommand command);
    /** Updates metadata of existing content. */
    void updateMetadata(UpdateMetadataCommand command);
    /** Removes content from the library. */
    void removeContent(ContentId contentId);
}
