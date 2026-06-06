package com.project.youtlix.contentlibrary.domain.model;

import java.util.ArrayList;
import java.util.List;

/** Entity representing a season inside a series. */
public class Season {
    private final int number;
    private final String title;
    private final List<Episode> episodes = new ArrayList<>();

    public Season(int number, String title) {
        if (number <= 0) throw new IllegalArgumentException("Season number must be positive");
        this.number = number;
        this.title = title;
    }

    /** Adds an episode to this season. */
    public void addEpisode(Episode episode) { episodes.add(episode); }
    public int number() { return number; }
    public String title() { return title; }
    public List<Episode> episodes() { return List.copyOf(episodes); }
}
