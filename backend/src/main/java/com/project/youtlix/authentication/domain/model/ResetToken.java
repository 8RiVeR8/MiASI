package com.project.youtlix.authentication.domain.model;

import java.util.UUID;

/** Value object representing a token used to reset a password. */
public record ResetToken(String value) {
    public ResetToken { if (value == null || value.isBlank()) throw new IllegalArgumentException("Reset token is required"); }
    /** Creates a random reset token. */
    public static ResetToken random() { return new ResetToken(UUID.randomUUID().toString()); }
}
