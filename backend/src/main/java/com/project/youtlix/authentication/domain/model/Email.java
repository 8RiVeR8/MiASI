package com.project.youtlix.authentication.domain.model;

/** Value object representing an email address. */
public record Email(String address) {
    public Email {
        if (address == null || address.isBlank()) throw new IllegalArgumentException("Email address is required");
        address = address.trim().toLowerCase();
        if (!address.contains("@")) throw new IllegalArgumentException("Email address must contain @");
    }
}
