package com.project.youtlix.videoplayback.infrastructure.in.web;

import com.project.youtlix.videoplayback.application.port.in.StartedPlayback;

import java.util.UUID;

/**
 * Response returned when playback opens a stream.
 */
public record PlaybackResponse(
        UUID playbackId,
        String uri,
        String language,
        int resumeFromSeconds,
        boolean resumed
) {

    /** Maps started playback to HTTP response DTO. */
    static PlaybackResponse from(StartedPlayback startedPlayback) {
        return new PlaybackResponse(
                startedPlayback.playbackId().value(),
                startedPlayback.stream().uri(),
                startedPlayback.stream().language(),
                startedPlayback.resumeFromSeconds(),
                startedPlayback.resumed()
        );
    }
}
