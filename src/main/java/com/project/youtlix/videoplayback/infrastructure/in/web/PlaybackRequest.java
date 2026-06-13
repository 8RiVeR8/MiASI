package com.project.youtlix.videoplayback.infrastructure.in.web;

/**
 * Request body for saving playback progress.
 */
public record PlaybackRequest(int positionSeconds) {
}