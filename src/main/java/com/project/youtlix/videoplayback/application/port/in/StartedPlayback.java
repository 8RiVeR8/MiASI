package com.project.youtlix.videoplayback.application.port.in;

import com.project.youtlix.videoplayback.domain.model.PlaybackId;
import com.project.youtlix.videoplayback.domain.model.VideoStream;

/**
 * Result of starting or resuming playback for PU14.
 */
public record StartedPlayback(
        VideoStream stream,
        PlaybackId playbackId,
        int resumeFromSeconds,
        boolean resumed
) {
}
