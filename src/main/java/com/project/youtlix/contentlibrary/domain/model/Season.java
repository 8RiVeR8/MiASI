package com.project.youtlix.contentlibrary.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Season entity inside a series aggregate.
 */
public class Season {

    private final SeasonId id;
    private final int number;
    private final String title;
    private final List<Episode> episodes = new ArrayList<>();

    /**
     * Creates a season entity.
     */
    public Season(int number, String title) {
        this(SeasonId.newId(), number, title);
    }

    /**
     * Recreates a season entity from persistence.
     */
    public Season(SeasonId id, int number, String title) {
        if (number <= 0) {
            throw new IllegalArgumentException("season number must be positive");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("season title must not be blank");
        }
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.number = number;
        this.title = title;
    }

    /**
     * Adds an episode to this season.
     *
     * @param episode episode entity
     */
    public void addEpisode(Episode episode) {
        Objects.requireNonNull(episode, "episode must not be null");
        if (episodes.stream().anyMatch(existing -> existing.number() == episode.number())) {
            throw new IllegalArgumentException("episode number already exists in season");
        }
        episodes.add(episode);
    }

    public SeasonId id() {
        return id;
    }

    public int number() {
        return number;
    }

    public String title() {
        return title;
    }

    public List<Episode> episodes() {
        return Collections.unmodifiableList(episodes);
    }
}
