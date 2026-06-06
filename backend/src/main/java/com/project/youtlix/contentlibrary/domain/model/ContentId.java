package com.project.youtlix.contentlibrary.domain.model;

import java.util.UUID;

/** Value object identifying content in the content library context. */
public record ContentId(UUID value) {
    public ContentId { if (value == null) throw new IllegalArgumentException("Content id is required"); }
    /** Creates a new content identifier. */
    public static ContentId newId() { return new ContentId(UUID.randomUUID()); }
}
