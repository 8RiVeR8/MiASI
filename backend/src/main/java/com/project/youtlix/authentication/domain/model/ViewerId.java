package com.project.youtlix.authentication.domain.model;

import java.util.UUID;

/** Value object identifying a viewer account. */
public record ViewerId(UUID value) {
    public ViewerId { if (value == null) throw new IllegalArgumentException("Viewer id is required"); }
    /** Creates a new viewer identifier. */
    public static ViewerId newId() { return new ViewerId(UUID.randomUUID()); }
}
