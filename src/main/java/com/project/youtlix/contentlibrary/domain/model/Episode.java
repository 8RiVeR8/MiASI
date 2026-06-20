package com.project.youtlix.contentlibrary.domain.model;

import java.util.Objects;

/**
 * Episode entity inside a season.
 */
public class Episode {

    private final EpisodeId id;
    private int number;
    private String title;
    private Duration duration;
    private VideoFile videoFile;

    /**
     * Creates an episode entity.
     */
    public Episode(EpisodeId id, int number, String title, Duration duration, VideoFile videoFile) {
        if (number <= 0) {
            throw new IllegalArgumentException("episode number must be positive");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("episode title must not be blank");
        }
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.number = number;
        this.title = title;
        this.duration = Objects.requireNonNull(duration, "duration must not be null");
        this.videoFile = Objects.requireNonNull(videoFile, "videoFile must not be null");
    }

    public EpisodeId id() {
        return id;
    }

    public int number() {
        return number;
    }

    public String title() {
        return title;
    }

    public Duration duration() {
        return duration;
    }

    public VideoFile videoFile() {
        return videoFile;
    }

    /**
     * Updates episode metadata and playback data.
     */
    public void updateDetails(int number, String title, Duration duration, VideoFile videoFile) {
        validateNumber(number);
        validateTitle(title);
        this.number = number;
        this.title = title;
        this.duration = Objects.requireNonNull(duration, "duration must not be null");
        this.videoFile = Objects.requireNonNull(videoFile, "videoFile must not be null");
    }

    private void validateNumber(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("episode number must be positive");
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("episode title must not be blank");
        }
    }
}
