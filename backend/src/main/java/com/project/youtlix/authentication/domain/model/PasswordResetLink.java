package com.project.youtlix.authentication.domain.model;

import com.project.youtlix.common.domain.DomainException;
import java.time.Instant;

/** Aggregate root representing a one-time password reset link. */
public class PasswordResetLink {
    private final ResetLinkId id;
    private final ViewerId viewerId;
    private final ResetToken token;
    private final Instant expiresAt;
    private boolean used;

    public PasswordResetLink(ResetLinkId id, ViewerId viewerId, ResetToken token, Instant expiresAt, boolean used) {
        this.id = id;
        this.viewerId = viewerId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.used = used;
    }

    /** Creates a new one-time link for the viewer. */
    public static PasswordResetLink create(ViewerId viewerId, Instant expiresAt) {
        return new PasswordResetLink(ResetLinkId.newId(), viewerId, ResetToken.random(), expiresAt, false);
    }

    /** Checks if the link can be used at the provided time. */
    public boolean isUsable(Instant now) { return !used && now.isBefore(expiresAt); }

    /** Marks the link as consumed. */
    public void consume() {
        if (used) throw new DomainException("Password reset link is already used");
        used = true;
    }

    public ResetLinkId id() { return id; }
    public ViewerId viewerId() { return viewerId; }
    public ResetToken token() { return token; }
}
