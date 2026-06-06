package com.project.youtlix.authentication.domain.model;

/** Value object storing a password hash. */
public record Password(String hash) {
    public Password { if (hash == null || hash.isBlank()) throw new IllegalArgumentException("Password hash is required"); }
    /** Creates a password value from raw input for the initial skeleton. */
    public static Password fromRaw(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) throw new IllegalArgumentException("Password is required");
        return new Password(rawPassword);
    }
    /** Verifies raw input against the stored placeholder hash. */
    public boolean verify(String rawPassword) { return hash.equals(rawPassword); }
}
