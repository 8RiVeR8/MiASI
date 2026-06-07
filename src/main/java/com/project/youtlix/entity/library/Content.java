package com.project.youtlix.entity.library;

import com.project.youtlix.entity.enums.ContentType;
import com.project.youtlix.entity.enums.Genre;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "library", name = "contents")
public class Content {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "content_type")
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(name = "genre")
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(name = "available")
    private Boolean available;
}
