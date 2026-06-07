package com.project.youtlix.videoplayback.infrastructure.in.web;

/**
 * Response returned when playback opens a stream.
 */
public record PlaybackResponse(String uri, String language) {
}
