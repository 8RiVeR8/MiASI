package com.project.youtlix.videoplayback.domain.model;

import java.util.UUID;

/** Value object identifying content in playback context. */
public record ContentId(UUID value) {
    public ContentId { if (value == null) throw new IllegalArgumentException("Content id is required"); }
}
