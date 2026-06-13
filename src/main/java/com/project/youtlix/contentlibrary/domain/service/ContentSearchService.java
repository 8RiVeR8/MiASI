package com.project.youtlix.contentlibrary.domain.service;

import com.project.youtlix.contentlibrary.domain.model.Content;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;

import java.util.List;

/**
 * Domain search rules used by application services after data is loaded by ports.
 */
public class ContentSearchService {

    /**
     * Keeps only content currently published in the catalog.
     */
    public List<Content> browse(List<Content> candidates) {
        return candidates.stream().filter(Content::available).toList();
    }

    /**
     * Searches already loaded content by title or keyword.
     */
    public List<Content> searchByKeyword(List<Content> candidates, String phrase) {
        return candidates.stream()
                .filter(Content::available)
                .filter(content -> phraseMatches(content, phrase))
                .toList();
    }

    /**
     * Filters already loaded content by phrase, genre and release year range.
     *
     * @param candidates contents loaded from repository
     * @param criteria filtering criteria
     * @return content matching criteria
     */
    public List<Content> filter(List<Content> candidates, SearchCriteria criteria) {
        return candidates.stream()
                .filter(Content::available)
                .filter(content -> criteria.genre() == null || content.metadata().genre() == criteria.genre())
                .filter(content -> criteria.yearFrom() == null || content.metadata().releaseYear() >= criteria.yearFrom())
                .filter(content -> criteria.yearTo() == null || content.metadata().releaseYear() <= criteria.yearTo())
                .filter(content -> phraseMatches(content, criteria.phrase()))
                .toList();
    }

    private boolean phraseMatches(Content content, String phrase) {
        if (phrase == null || phrase.isBlank()) {
            return true;
        }
        String normalized = phrase.trim().toLowerCase();
        return content.metadata().title().toLowerCase().contains(normalized)
                || content.metadata().keywords().stream().map(Keyword::value).anyMatch(value -> value.contains(normalized));
    }
}
