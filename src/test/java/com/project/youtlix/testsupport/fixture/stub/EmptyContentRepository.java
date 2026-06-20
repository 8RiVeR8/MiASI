package com.project.youtlix.testsupport.fixture.stub;

import com.project.youtlix.contentlibrary.application.port.in.ResolvedPlayable;
import com.project.youtlix.contentlibrary.application.port.out.ContentRepository;
import com.project.youtlix.contentlibrary.domain.model.Content;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Page;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class EmptyContentRepository implements ContentRepository {

    @Override
    public void save(Content content) {
    }

    @Override
    public Optional<Content> ofId(ContentId id) {
        return Optional.empty();
    }

    @Override
    public List<Content> matching(SearchCriteria criteria) {
        return List.of();
    }

    @Override
    public List<Content> byKeyword(String phrase) {
        return List.of();
    }

    @Override
    public List<Content> page(Page page) {
        return List.of();
    }

    @Override
    public List<ContentId> popularContent(int limit) {
        return List.of();
    }

    @Override
    public Optional<VideoFile> videoFileOf(ContentId id) {
        return Optional.empty();
    }

    @Override
    public Optional<ResolvedPlayable> resolvePlayable(UUID id) {
        return Optional.empty();
    }

    @Override
    public boolean isSeries(ContentId id) {
        return false;
    }

    @Override
    public void remove(ContentId id) {
    }
}
