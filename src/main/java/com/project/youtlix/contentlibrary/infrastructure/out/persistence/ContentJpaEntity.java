package com.project.youtlix.contentlibrary.infrastructure.out.persistence;

import com.project.youtlix.contentlibrary.domain.model.Genre;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * JPA row mapped to library.contents in Supabase Postgres.
 */
@Entity
@Table(schema = "library", name = "contents")
public class ContentJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "genre", nullable = false)
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(name = "release_year", nullable = false)
    private Integer releaseYear;

    @Column(name = "available", nullable = false)
    private Boolean available;

    protected ContentJpaEntity() {
    }

    /**
     * Creates a content persistence row.
     */
    public ContentJpaEntity(
            UUID id,
            ContentType contentType,
            String title,
            String description,
            String thumbnailUrl,
            Genre genre,
            Integer releaseYear,
            Boolean available
    ) {
        this.id = id;
        this.contentType = contentType;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.available = available;
    }

    public UUID id() {
        return id;
    }

    public ContentType contentType() {
        return contentType;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public String thumbnailUrl() {
        return thumbnailUrl;
    }

    public Genre genre() {
        return genre;
    }

    public Integer releaseYear() {
        return releaseYear;
    }

    public Boolean available() {
        return available;
    }
}
