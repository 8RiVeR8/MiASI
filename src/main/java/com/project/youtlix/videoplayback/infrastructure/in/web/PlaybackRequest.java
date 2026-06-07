package com.project.youtlix.videoplayback.infrastructure.in.web;

import java.util.UUID;

/**
 * Request body for saving playback progress.
 */
public record PlaybackRequest(UUID playbackId, int positionSeconds) {
}
