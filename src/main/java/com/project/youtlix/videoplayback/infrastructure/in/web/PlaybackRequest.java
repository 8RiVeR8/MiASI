package com.project.youtlix.videoplayback.infrastructure.in.web;

import java.util.UUID;

/**
 * Request body for saving or completing playback progress.
 */
public record PlaybackRequest(UUID playbackId, int positionSeconds) {
}
