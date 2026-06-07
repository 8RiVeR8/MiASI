package com.project.youtlix.authentication.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Internal identifier of a viewer mapped from the Supabase user id.
 *
 * @param value Supabase Auth user UUID
 */
public record ViewerId(UUID value) {

    /**
     * Creates a validated viewer id.
     */
    public ViewerId {
        Objects.requireNonNull(value, "viewer id value must not be null");
    }
}
