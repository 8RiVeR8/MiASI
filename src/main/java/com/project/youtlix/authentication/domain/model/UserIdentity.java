package com.project.youtlix.authentication.domain.model;

import java.util.Objects;

/**
 * Published identity model used by all Youtlix modules instead of Supabase types.
 *
 * @param viewerId authenticated viewer identifier
 * @param role role assigned to the viewer
 */
public record UserIdentity(ViewerId viewerId, Role role) {

    /**
     * Creates a validated identity value object.
     */
    public UserIdentity {
        Objects.requireNonNull(viewerId, "viewerId must not be null");
        Objects.requireNonNull(role, "role must not be null");
    }

    /**
     * Checks whether identity can manage the content library.
     *
     * @return true for library administrators
     */
    public boolean canManageLibrary() {
        return role == Role.LIBRARY_ADMIN;
    }
}
