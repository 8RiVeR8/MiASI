package com.project.youtlix.recommendation.application.port.out;

import com.project.youtlix.contentlibrary.domain.model.ContentId;

import java.util.List;

/**
 * Outbound port to content catalog data consumed by recommendation strategies.
 */
public interface ContentCatalogPort {

    /** Returns popular content ids. */
    List<ContentId> popularContent(int limit);

    /** Returns metadata of a selected content item. */
    ContentMetadata metadataOf(ContentId id);
}
