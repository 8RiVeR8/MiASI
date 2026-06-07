package com.project.youtlix.contentlibrary.domain.model;

import java.util.Objects;

/**
 * Movie aggregate root with one video file and duration.
 */
public class Movie extends Content {

    private final Duration duration;
    private final VideoFile videoFile;

    /**
     * Creates a movie aggregate.
     */
    public Movie(ContentId id, Metadata metadata, Duration duration, VideoFile videoFile) {
        super(id, metadata);
        this.duration = Objects.requireNonNull(duration, "duration must not be null");
        this.videoFile = Objects.requireNonNull(videoFile, "videoFile must not be null");
    }

    public Duration duration() {
        return duration;
    }

    public VideoFile videoFile() {
        return videoFile;
    }
}
