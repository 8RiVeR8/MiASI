package com.project.youtlix.recommendation.infrastructure.out.content;

import com.project.youtlix.contentlibrary.application.port.in.ContentCatalogApi;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.recommendation.application.port.out.ContentCatalogPort;
import com.project.youtlix.recommendation.application.port.out.ContentMetadata;
import com.project.youtlix.recommendation.domain.model.ContentId;
import org.springframework.stereotype.Component;
import java.util.List;

/** In-process conformist adapter from recommendation to content library catalog. */
@Component
public class ContentCatalogInProcessAdapter implements ContentCatalogPort {
    private final ContentCatalogApi catalog;
    public ContentCatalogInProcessAdapter(ContentCatalogApi catalog) { this.catalog = catalog; }
    @Override public List<ContentId> popularContent(int limit) {
        return catalog.popularContent(limit).stream().map(id -> new ContentId(id.value())).toList();
    }
    @Override public ContentMetadata metadataOf(ContentId id) {
        var metadata = catalog.metadataOf(new com.project.youtlix.contentlibrary.domain.model.ContentId(id.value()));
        return new ContentMetadata(metadata.genre().name(), metadata.keywords().stream().map(Keyword::value).toList(), metadata.releaseYear());
    }
}
