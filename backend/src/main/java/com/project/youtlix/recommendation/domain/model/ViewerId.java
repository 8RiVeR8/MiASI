package com.project.youtlix.recommendation.domain.model;

import java.util.UUID;

/** Value object identifying a viewer in the recommendation context. */
public record ViewerId(UUID value) {
    public ViewerId { if (value == null) throw new IllegalArgumentException("Viewer id is required"); }
}
