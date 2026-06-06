package com.project.youtlix.videoplayback.domain.model;

import java.util.UUID;

/** Value object identifying a viewer in playback context. */
public record ViewerId(UUID value) {
    public ViewerId { if (value == null) throw new IllegalArgumentException("Viewer id is required"); }
}
