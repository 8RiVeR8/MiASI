package com.project.youtlix.testsupport.fixture.stub;

import com.project.youtlix.contentlibrary.application.port.in.ContentCatalogApi;
import com.project.youtlix.contentlibrary.application.port.in.ContentMetadata;
import com.project.youtlix.contentlibrary.application.port.in.ResolvedPlayable;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;

import java.util.List;
import java.util.UUID;

public final class FakePlaybackContentCatalogApi implements ContentCatalogApi {

    @Override
    public List<ContentId> popularContent(int limit) {
        return List.of();
    }

    @Override
    public ContentMetadata metadataOf(ContentId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VideoFile videoFileOf(ContentId id) {
        return new VideoFile("cdn://movie", List.of("pl"));
    }

    @Override
    public ResolvedPlayable resolvePlayable(UUID id) {
        return new ResolvedPlayable(
                id,
                ResolvedPlayable.PlayableKind.MOVIE,
                new VideoFile("cdn://movie", List.of("pl"))
        );
    }
}
