package com.project.youtlix.recommendation.application.port.out;

import com.project.youtlix.recommendation.domain.model.ContentId;
import java.util.List;

/** Output port to content catalog data used by recommendation strategies. */
public interface ContentCatalogPort {
    List<ContentId> popularContent(int limit);
    ContentMetadata metadataOf(ContentId id);
}
