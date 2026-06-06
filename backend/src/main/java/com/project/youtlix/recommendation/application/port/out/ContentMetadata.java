package com.project.youtlix.recommendation.application.port.out;

import java.util.List;

/** Content metadata consumed by recommendation as a conformist port contract. */
public record ContentMetadata(String genre, List<String> keywords, int releaseYear) {}
