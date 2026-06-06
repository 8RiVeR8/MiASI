package com.project.youtlix.contentlibrary.infrastructure.out.persistence;

import com.project.youtlix.contentlibrary.application.port.out.ContentRepository;
import com.project.youtlix.contentlibrary.domain.model.*;
import org.springframework.stereotype.Repository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Temporary in-memory adapter for content until SQL persistence is implemented. */
@Repository
public class InMemoryContentRepository implements ContentRepository {
    private final Map<ContentId, Content> content = new ConcurrentHashMap<>();

    @Override public void save(Content item) { content.put(item.id(), item); }
    @Override public Optional<Content> ofId(ContentId id) { return Optional.ofNullable(content.get(id)); }
    @Override public void remove(ContentId id) { content.remove(id); }

    @Override public List<Content> page(Page page) {
        return content.values().stream().filter(Content::available)
                .sorted(Comparator.comparing(c -> c.metadata().title()))
                .skip((long) page.number() * page.size()).limit(page.size()).toList();
    }

    @Override public List<Content> byKeyword(String phrase) {
        String normalized = phrase == null ? "" : phrase.toLowerCase();
        return content.values().stream().filter(Content::available)
                .filter(c -> c.metadata().title().toLowerCase().contains(normalized)
                        || c.metadata().keywords().stream().anyMatch(k -> k.value().contains(normalized)))
                .toList();
    }

    @Override public List<Content> matching(SearchCriteria criteria) {
        return content.values().stream().filter(Content::available)
                .filter(c -> criteria.genre() == null || c.metadata().genre() == criteria.genre())
                .filter(c -> criteria.yearFrom() == null || c.metadata().releaseYear() >= criteria.yearFrom())
                .filter(c -> criteria.yearTo() == null || c.metadata().releaseYear() <= criteria.yearTo())
                .filter(c -> criteria.phrase() == null || criteria.phrase().isBlank() || c.metadata().title().toLowerCase().contains(criteria.phrase().toLowerCase()))
                .toList();
    }
}
