package com.project.youtlix.contentlibrary.application.port.in;

import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import java.util.List;

/** Published content metadata used by conformist downstream contexts. */
public record ContentMetadata(String title, Genre genre, List<Keyword> keywords, int releaseYear) {}
