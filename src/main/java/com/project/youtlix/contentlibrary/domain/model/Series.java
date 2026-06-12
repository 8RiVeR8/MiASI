package com.project.youtlix.contentlibrary.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Series aggregate root composed of seasons and episodes.
 */
public class Series extends Content {

    private final List<Season> seasons = new ArrayList<>();

    /**
     * Creates a series aggregate.
     */
    public Series(ContentId id, Metadata metadata) {
        super(id, metadata);
    }

    /**
     * Recreates a series aggregate from persistence.
     */
    public Series(ContentId id, Metadata metadata, boolean available, boolean recordAddedEvent) {
        super(id, metadata, available, recordAddedEvent);
    }

    /**
     * Adds a season to the series aggregate.
     *
     * @param season season entity
     */
    public void addSeason(Season season) {
        seasons.add(season);
    }

    /**
     * Counts episodes in all seasons.
     *
     * @return episode count
     */
    public int episodeCount() {
        return seasons.stream().mapToInt(season -> season.episodes().size()).sum();
    }

    public List<Season> seasons() {
        return Collections.unmodifiableList(seasons);
    }
}
