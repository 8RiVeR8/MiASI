package com.project.youtlix.authentication.domain.model;

/** Value object representing a token returned after successful login. */
public record SessionToken(String value) {
    public SessionToken { if (value == null || value.isBlank()) throw new IllegalArgumentException("Session token is required"); }
    /** Creates a token derived from a session id for the initial in-process skeleton. */
    public static SessionToken fromSessionId(SessionId sessionId) { return new SessionToken(sessionId.value().toString()); }
}
