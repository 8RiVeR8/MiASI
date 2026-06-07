package com.project.youtlix.videoplayback.infrastructure.out.persistence;

import com.project.youtlix.videoplayback.domain.model.PlaybackStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA row mapped to playback.playbacks in Supabase Postgres.
 */
@Entity
@Table(
        schema = "playback",
        name = "playbacks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"viewer_id", "playable_type", "playable_id"})
)
public class PlaybackJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "viewer_id", nullable = false)
    private UUID viewerId;

    @Column(name = "playable_id", nullable = false)
    private UUID contentId;

    @Column(name = "playable_type", nullable = false)
    private String playableType;

    @Column(name = "position_seconds", nullable = false)
    private Integer positionSeconds;

    @Column(name = "progress_updated_at", nullable = false)
    private Instant progressUpdatedAt;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlaybackStatus status;

    protected PlaybackJpaEntity() {
    }

    /** Creates a playback persistence row. */
    public PlaybackJpaEntity(
            UUID id,
            UUID viewerId,
            UUID contentId,
            String playableType,
            Integer positionSeconds,
            Instant progressUpdatedAt,
            PlaybackStatus status
    ) {
        this.id = id;
        this.viewerId = viewerId;
        this.contentId = contentId;
        this.playableType = playableType;
        this.positionSeconds = positionSeconds;
        this.progressUpdatedAt = progressUpdatedAt;
        this.status = status;
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

    public Integer positionSeconds() {
        return positionSeconds;
    }

    public Instant progressUpdatedAt() {
        return progressUpdatedAt;
    }

    public PlaybackStatus status() {
        return status;
    }
}
