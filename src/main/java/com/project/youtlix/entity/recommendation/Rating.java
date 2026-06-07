package com.project.youtlix.entity.recommendation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "recommendation", name = "ratings", uniqueConstraints = {@UniqueConstraint(columnNames = {"viewer_id", "content_id"})})
public class Rating {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "viewer_id", nullable = false)
    private UUID viewerId;

    @Column(name = "content_id", nullable = false)
    private UUID contentId;

    @Column(name = "stars", nullable = false)
    private Short stars;

    @Column(name = "rated_at")
    private LocalDateTime ratedAt;
}
