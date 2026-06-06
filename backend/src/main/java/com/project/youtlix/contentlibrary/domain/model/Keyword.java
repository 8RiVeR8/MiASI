package com.project.youtlix.contentlibrary.domain.model;

/** Value object representing a metadata keyword. */
public record Keyword(String value) {
    public Keyword { if (value == null || value.isBlank()) throw new IllegalArgumentException("Keyword is required"); value = value.trim().toLowerCase(); }
}
