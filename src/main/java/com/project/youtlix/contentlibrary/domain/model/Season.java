package com.project.youtlix.contentlibrary.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Season entity inside a series aggregate.
 */
public class Season {

    private final int number;
    private final String title;
    private final List<Episode> episodes = new ArrayList<>();

    /**
     * Creates a season entity.
     */
    public Season(int number, String title) {
        if (number <= 0) {
            throw new IllegalArgumentException("season number must be positive");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("season title must not be blank");
        }
        this.number = number;
        this.title = title;
    }

    /**
     * Adds an episode to this season.
     *
     * @param episode episode entity
     */
    public void addEpisode(Episode episode) {
        episodes.add(episode);
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
