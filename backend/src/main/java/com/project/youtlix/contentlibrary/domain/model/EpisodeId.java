package com.project.youtlix.contentlibrary.domain.model;

import java.util.UUID;

/** Value object identifying an episode. */
public record EpisodeId(UUID value) {
    public EpisodeId { if (value == null) throw new IllegalArgumentException("Episode id is required"); }
    /** Creates a new episode identifier. */
    public static EpisodeId newId() { return new EpisodeId(UUID.randomUUID()); }
}
