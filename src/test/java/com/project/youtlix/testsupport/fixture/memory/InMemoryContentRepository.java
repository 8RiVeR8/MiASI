package com.project.youtlix.testsupport.fixture.memory;

import com.project.youtlix.contentlibrary.application.port.in.ResolvedPlayable;
import com.project.youtlix.contentlibrary.application.port.out.ContentRepository;
import com.project.youtlix.contentlibrary.domain.model.Content;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Movie;
import com.project.youtlix.contentlibrary.domain.model.Page;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;
import com.project.youtlix.contentlibrary.domain.model.Series;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class InMemoryContentRepository implements ContentRepository {

    private final Map<ContentId, Content> contents = new LinkedHashMap<>();
    public String lastKeywordPhrase;
    public SearchCriteria lastMatchingCriteria;

    @Override
    public void save(Content content) {
        content.publish();
        contents.put(content.id(), content);
    }

    @Override
    public Optional<Content> ofId(ContentId id) {
        return Optional.ofNullable(contents.get(id));
    }

    @Override
    public List<Content> matching(SearchCriteria criteria) {
        lastMatchingCriteria = criteria;
        return new ArrayList<>(contents.values());
    }

    @Override
    public List<Content> byKeyword(String phrase) {
        lastKeywordPhrase = phrase;
        return new ArrayList<>(contents.values());
    }

    @Override
    public List<Content> page(Page page) {
        return new ArrayList<>(contents.values());
    }

    @Override
    public List<ContentId> popularContent(int limit) {
        return contents.keySet().stream().limit(limit).toList();
    }

    @Override
    public Optional<VideoFile> videoFileOf(ContentId id) {
        return Optional.ofNullable(contents.get(id))
                .filter(Movie.class::isInstance)
                .map(Movie.class::cast)
                .map(Movie::videoFile);
    }

    @Override
    public Optional<ResolvedPlayable> resolvePlayable(UUID id) {
        Optional<VideoFile> movieVideo = videoFileOf(new ContentId(id));
        if (movieVideo.isPresent()) {
            return movieVideo.map(videoFile ->
                    new ResolvedPlayable(id, ResolvedPlayable.PlayableKind.MOVIE, videoFile));
        }
        return contents.values()
                .stream()
                .filter(Series.class::isInstance)
                .map(Series.class::cast)
                .flatMap(series -> series.seasons().stream())
                .flatMap(season -> season.episodes().stream())
                .filter(episode -> episode.id().value().equals(id))
                .findFirst()
                .map(episode ->
                        new ResolvedPlayable(id, ResolvedPlayable.PlayableKind.EPISODE, episode.videoFile()));
    }

    @Override
    public boolean isSeries(ContentId id) {
        return Optional.ofNullable(contents.get(id))
                .map(Series.class::isInstance)
                .orElse(false);
    }

    @Override
    public void remove(ContentId id) {
        contents.remove(id);
    }

    public boolean contains(ContentId id) {
        return contents.containsKey(id);
    }
}
