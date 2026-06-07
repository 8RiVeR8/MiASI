package com.project.youtlix.contentlibrary.domain.model;

/**
 * Criteria used for content filtering.
 *
 * @param phrase optional text phrase
 * @param genre optional genre
 * @param yearFrom optional lower year bound
 * @param yearTo optional upper year bound
 */
public record SearchCriteria(String phrase, Genre genre, Integer yearFrom, Integer yearTo) {

    /**
     * Creates validated filtering criteria.
     */
    public SearchCriteria {
        if (yearFrom != null && yearTo != null && yearFrom > yearTo) {
            throw new IllegalArgumentException("yearFrom must be lower than or equal to yearTo");
        }
    }
}
