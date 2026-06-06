package com.project.youtlix.contentlibrary.domain.model;

import java.util.ArrayList;
import java.util.List;

/** Aggregate root representing a series composed of seasons and episodes. */
public class Series extends Content {
    private final List<Season> seasons = new ArrayList<>();

    public Series(ContentId id, Metadata metadata) { super(id, metadata); }

    /** Adds a season to the series. */
    public void addSeason(Season season) { seasons.add(season); }
    /** Counts all episodes across seasons. */
    public int episodeCount() { return seasons.stream().mapToInt(season -> season.episodes().size()).sum(); }
    public List<Season> seasons() { return List.copyOf(seasons); }
}
