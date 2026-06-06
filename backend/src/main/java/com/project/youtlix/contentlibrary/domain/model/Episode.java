package com.project.youtlix.contentlibrary.domain.model;

/** Entity representing an episode inside a season. */
public class Episode {
    private final EpisodeId id;
    private final int number;
    private final String title;
    private final Duration duration;
    private final VideoFile videoFile;

    public Episode(EpisodeId id, int number, String title, Duration duration, VideoFile videoFile) {
        if (number <= 0) throw new IllegalArgumentException("Episode number must be positive");
        this.id = id;
        this.number = number;
        this.title = title;
        this.duration = duration;
        this.videoFile = videoFile;
    }

    public EpisodeId id() { return id; }
    public int number() { return number; }
    public String title() { return title; }
    public Duration duration() { return duration; }
    public VideoFile videoFile() { return videoFile; }
}
