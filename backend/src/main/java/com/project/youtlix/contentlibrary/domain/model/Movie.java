package com.project.youtlix.contentlibrary.domain.model;

/** Aggregate root representing a movie. */
public class Movie extends Content {
    private final Duration duration;
    private final VideoFile videoFile;

    public Movie(ContentId id, Metadata metadata, Duration duration, VideoFile videoFile) {
        super(id, metadata);
        this.duration = duration;
        this.videoFile = videoFile;
    }

    public Duration duration() { return duration; }
    public VideoFile videoFile() { return videoFile; }
}
