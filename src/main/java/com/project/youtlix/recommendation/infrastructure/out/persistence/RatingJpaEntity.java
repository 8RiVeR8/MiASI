package com.project.youtlix.recommendation.infrastructure.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA row mapped to recommendation.ratings in Supabase Postgres.
 */
@Entity
@Table(
        schema = "recommendation",
        name = "ratings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"viewer_id", "content_id"})
)
public class RatingJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "viewer_id", nullable = false)
    private UUID viewerId;

    @Column(name = "content_id", nullable = false)
    private UUID contentId;

    @Column(name = "stars", nullable = false)
    private Short stars;

    @Column(name = "rated_at", nullable = false)
    private Instant ratedAt;

    protected RatingJpaEntity() {
    }

    /** Creates rating persistence row. */
    public RatingJpaEntity(UUID id, UUID viewerId, UUID contentId, Short stars, Instant ratedAt) {
        this.id = id;
        this.viewerId = viewerId;
        this.contentId = contentId;
        this.stars = stars;
        this.ratedAt = ratedAt;
    }

    public UUID id() {
        return id;
    }

    public UUID viewerId() {
        return viewerId;
    }

    public UUID contentId() {
        return contentId;
    }

    public Short stars() {
        return stars;
    }

    public Instant ratedAt() {
        return ratedAt;
    }
}
