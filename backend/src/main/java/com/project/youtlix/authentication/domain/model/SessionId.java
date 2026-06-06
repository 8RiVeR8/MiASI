package com.project.youtlix.authentication.domain.model;

import java.util.UUID;

/** Value object identifying an authenticated session. */
public record SessionId(UUID value) {
    public SessionId { if (value == null) throw new IllegalArgumentException("Session id is required"); }
    /** Creates a new session identifier. */
    public static SessionId newId() { return new SessionId(UUID.randomUUID()); }
}
