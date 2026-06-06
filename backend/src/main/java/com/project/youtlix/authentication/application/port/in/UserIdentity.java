package com.project.youtlix.authentication.application.port.in;

import java.util.UUID;

/** Published language identity shared with downstream contexts. */
public record UserIdentity(UUID viewerId, Role role) {
    public UserIdentity {
        if (viewerId == null) throw new IllegalArgumentException("Viewer id is required");
        if (role == null) throw new IllegalArgumentException("Role is required");
    }
}
