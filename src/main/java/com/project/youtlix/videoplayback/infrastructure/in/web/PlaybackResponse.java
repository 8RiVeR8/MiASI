package com.project.youtlix.videoplayback.infrastructure.in.web;

import com.project.youtlix.videoplayback.domain.model.VideoStream;

/**
 * Response returned when playback opens a stream.
 */
public record PlaybackResponse(String uri, String language) {

    /** Maps opened stream to HTTP response DTO. */
    static PlaybackResponse from(VideoStream stream) {
        return new PlaybackResponse(stream.uri(), stream.language());
    }
}
