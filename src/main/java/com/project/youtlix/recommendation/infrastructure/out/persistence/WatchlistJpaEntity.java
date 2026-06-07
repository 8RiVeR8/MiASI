package com.project.youtlix.recommendation.infrastructure.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * JPA row mapped to recommendation.watchlists in Supabase Postgres.
 */
@Entity
@Table(schema = "recommendation", name = "watchlists")
public class WatchlistJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "viewer_id", nullable = false, unique = true)
    private UUID viewerId;

    protected WatchlistJpaEntity() {
    }

    /** Creates watchlist persistence row. */
    public WatchlistJpaEntity(UUID id, UUID viewerId) {
        this.id = id;
        this.viewerId = viewerId;
    }

    public UUID id() {
        return id;
    }

    public UUID viewerId() {
        return viewerId;
    }
}
