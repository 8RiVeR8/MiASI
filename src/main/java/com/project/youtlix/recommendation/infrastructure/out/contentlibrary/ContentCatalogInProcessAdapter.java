package com.project.youtlix.recommendation.infrastructure.out.contentlibrary;

import com.project.youtlix.contentlibrary.application.port.in.ContentCatalogApi;
import com.project.youtlix.contentlibrary.application.port.in.ContentMetadata;
import com.project.youtlix.recommendation.application.port.out.ContentCatalogPort;
import com.project.youtlix.recommendation.domain.model.ContentId;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * In-process conformist adapter from recommendation to content library OHS.
 */
@Component
public class ContentCatalogInProcessAdapter implements ContentCatalogPort {

    private final ContentCatalogApi contentCatalogApi;

    /** Creates adapter around the library published port. */
    public ContentCatalogInProcessAdapter(ContentCatalogApi contentCatalogApi) {
        this.contentCatalogApi = contentCatalogApi;
    }

    @Override
    public List<ContentId> popularContent(int limit) {
        return contentCatalogApi.popularContent(limit).stream()
                .map(id -> new ContentId(id.value()))
                .toList();
    }

    @Override
    public ContentMetadata metadataOf(ContentId id) {
        return contentCatalogApi.metadataOf(new com.project.youtlix.contentlibrary.domain.model.ContentId(id.value()));
    }
}
