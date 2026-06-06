package com.project.youtlix.contentlibrary.domain.model;

/** Value object containing PU7 filtering criteria. */
public record SearchCriteria(String phrase, Genre genre, Integer yearFrom, Integer yearTo) {}
