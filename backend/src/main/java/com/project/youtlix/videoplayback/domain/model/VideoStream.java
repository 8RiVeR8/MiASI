package com.project.youtlix.videoplayback.domain.model;

/** Value object returned to a player when a video stream is opened. */
public record VideoStream(String uri, String language) {
    public VideoStream { if (uri == null || uri.isBlank()) throw new IllegalArgumentException("Stream uri is required"); }
}
