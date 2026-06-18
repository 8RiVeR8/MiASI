package com.project.youtlix.contentlibrary.application.port.in;

import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;

import java.util.List;

/**
 * Open Host Service published by the library for playback and recommendation modules.
 */
public interface ContentCatalogApi {

    /** Returns popular content identifiers. */
    List<ContentId> popularContent(int limit);

    /** Returns published metadata for a content id. */
    ContentMetadata metadataOf(ContentId id);

    /** Returns technical video file data for playback. */
    VideoFile videoFileOf(ContentId id);

    /** Resolves a movie or episode id for playback. */
    ResolvedPlayable resolvePlayable(java.util.UUID id);
}
