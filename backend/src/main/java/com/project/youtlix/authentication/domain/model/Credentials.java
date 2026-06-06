package com.project.youtlix.authentication.domain.model;

/** Value object grouping login credentials. */
public record Credentials(Email email, String rawPassword) {
    public Credentials {
        if (email == null) throw new IllegalArgumentException("Credentials email is required");
        if (rawPassword == null || rawPassword.isBlank()) throw new IllegalArgumentException("Credentials password is required");
    }
}
