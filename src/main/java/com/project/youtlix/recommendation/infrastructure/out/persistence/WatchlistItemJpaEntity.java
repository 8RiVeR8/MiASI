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
 * JPA row mapped to recommendation.watchlist_items in Supabase Postgres.
 */
@Entity
@Table(
        schema = "recommendation",
        name = "watchlist_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"watchlist_id", "content_id"})
)
public class WatchlistItemJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "watchlist_id", nullable = false)
    private UUID watchlistId;

    @Column(name = "content_id", nullable = false)
    private UUID contentId;

    @Column(name = "added_on", nullable = false)
    private Instant addedOn;

    protected WatchlistItemJpaEntity() {
    }

    /** Creates watchlist item persistence row. */
    public WatchlistItemJpaEntity(UUID id, UUID watchlistId, UUID contentId, Instant addedOn) {
        this.id = id;
        this.watchlistId = watchlistId;
        this.contentId = contentId;
        this.addedOn = addedOn;
    }

    public UUID id() {
        return id;
    }

    public UUID watchlistId() {
        return watchlistId;
    }

    public UUID contentId() {
        return contentId;
    }

    public Instant addedOn() {
        return addedOn;
    }
}
