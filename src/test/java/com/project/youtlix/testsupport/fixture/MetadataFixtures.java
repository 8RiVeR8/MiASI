package com.project.youtlix.testsupport.fixture;

import com.project.youtlix.contentlibrary.domain.model.ContentType;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.contentlibrary.domain.model.Metadata;

import java.util.List;

public final class MetadataFixtures {

    private MetadataFixtures() {
    }

    public static Metadata movie(String title, String description, String thumbnailUrl, Genre genre, int releaseYear, List<Keyword> keywords) {
        return new Metadata(title, description, ContentType.MOVIE, thumbnailUrl, genre, releaseYear, keywords);
    }

    public static Metadata series(String title, String description, String thumbnailUrl, Genre genre, int releaseYear, List<Keyword> keywords) {
        return new Metadata(title, description, ContentType.SERIES, thumbnailUrl, genre, releaseYear, keywords);
    }
}
