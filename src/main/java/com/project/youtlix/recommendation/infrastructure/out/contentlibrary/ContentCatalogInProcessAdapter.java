package com.project.youtlix.recommendation.infrastructure.out.contentlibrary;

import com.project.youtlix.contentlibrary.application.port.in.ContentCatalogApi;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.recommendation.application.port.out.ContentCatalogPort;
import com.project.youtlix.recommendation.application.port.out.ContentMetadata;
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
        return contentCatalogApi.popularContent(limit);
    }

    @Override
    public ContentMetadata metadataOf(ContentId id) {
        com.project.youtlix.contentlibrary.application.port.in.ContentMetadata metadata = contentCatalogApi.metadataOf(id);
        return new ContentMetadata(
                metadata.genre().name(),
                metadata.keywords().stream().map(Keyword::value).toList(),
                metadata.releaseYear()
        );
    }
}
