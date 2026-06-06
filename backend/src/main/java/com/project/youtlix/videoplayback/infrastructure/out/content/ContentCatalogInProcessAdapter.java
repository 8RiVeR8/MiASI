package com.project.youtlix.videoplayback.infrastructure.out.content;

import com.project.youtlix.contentlibrary.application.port.in.ContentCatalogApi;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.videoplayback.application.port.out.ContentCatalogPort;
import com.project.youtlix.videoplayback.domain.model.ContentId;
import org.springframework.stereotype.Component;

/** In-process conformist adapter from playback to content library catalog. */
@Component
public class ContentCatalogInProcessAdapter implements ContentCatalogPort {
    private final ContentCatalogApi catalog;
    public ContentCatalogInProcessAdapter(ContentCatalogApi catalog) { this.catalog = catalog; }
    @Override public VideoFile videoFileOf(ContentId contentId) {
        return catalog.videoFileOf(new com.project.youtlix.contentlibrary.domain.model.ContentId(contentId.value()));
    }
}
