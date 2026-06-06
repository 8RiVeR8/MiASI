package com.project.youtlix.recommendation.domain.model;

import java.util.UUID;

/** Value object identifying content in the recommendation context. */
public record ContentId(UUID value) {
    public ContentId { if (value == null) throw new IllegalArgumentException("Content id is required"); }
}
