package com.project.youtlix.contentlibrary.application.port.in;

import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import java.util.List;

/** Open Host Service exposing content data to playback and recommendation contexts. */
public interface ContentCatalogApi {
    /** Returns technical video data for playback. */
    VideoFile videoFileOf(ContentId id);
    /** Returns globally popular content identifiers. */
    List<ContentId> popularContent(int limit);
    /** Returns published metadata for recommendation. */
    ContentMetadata metadataOf(ContentId id);
}
