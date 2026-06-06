package com.project.youtlix.authentication.domain.model;

import java.util.UUID;

/** Value object identifying a password reset link. */
public record ResetLinkId(UUID value) {
    public ResetLinkId { if (value == null) throw new IllegalArgumentException("Reset link id is required"); }
    /** Creates a new reset link identifier. */
    public static ResetLinkId newId() { return new ResetLinkId(UUID.randomUUID()); }
}
