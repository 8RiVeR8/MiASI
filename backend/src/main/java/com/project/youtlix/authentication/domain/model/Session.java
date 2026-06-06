package com.project.youtlix.authentication.domain.model;

import com.project.youtlix.authentication.domain.model.event.ViewerLoggedIn;
import com.project.youtlix.authentication.domain.model.event.ViewerLoggedOut;
import com.project.youtlix.common.domain.DomainEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Aggregate root representing an authenticated viewer session. */
public class Session {
    private final SessionId id;
    private final ViewerId viewerId;
    private final SessionToken token;
    private final Instant createdAt;
    private final Instant expiresAt;
    private boolean invalidated;
    private final List<DomainEvent> events = new ArrayList<>();

    public Session(SessionId id, ViewerId viewerId, SessionToken token, Instant createdAt, Instant expiresAt) {
        this.id = id;
        this.viewerId = viewerId;
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        events.add(new ViewerLoggedIn(viewerId, id, createdAt));
    }

    /** Creates a new session valid until the provided expiration time. */
    public static Session create(ViewerId viewerId, Instant expiresAt) {
        SessionId sessionId = SessionId.newId();
        return new Session(sessionId, viewerId, SessionToken.fromSessionId(sessionId), Instant.now(), expiresAt);
    }

    /** Checks if the session can still be used. */
    public boolean isValid(Instant now) { return !invalidated && now.isBefore(expiresAt); }

    /** Invalidates the session and records a logout event. */
    public void invalidate() {
        if (!invalidated) {
            invalidated = true;
            events.add(new ViewerLoggedOut(id, Instant.now()));
        }
    }

    /** Returns domain events recorded by this aggregate. */
    public List<DomainEvent> occurredEvents() { return List.copyOf(events); }
    public SessionId id() { return id; }
    public ViewerId viewerId() { return viewerId; }
    public SessionToken token() { return token; }
    public Instant createdAt() { return createdAt; }
    public Instant expiresAt() { return expiresAt; }
}
