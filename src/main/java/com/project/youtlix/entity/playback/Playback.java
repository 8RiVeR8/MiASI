package com.project.youtlix.entity.playback;

import com.project.youtlix.entity.enums.PlaybackStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "playback", name = "playbacks", uniqueConstraints = {@UniqueConstraint(columnNames = {"viewer_id", "playable_type", "playable_id"})})
public class Playback {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "viewer_id", nullable = false)
    private UUID viewerId;

    @Column(name = "playable_id", nullable = false)
    private UUID playableId;

    @Column(name = "playable_type", nullable = false)
    private String playableType;

    @Column(name = "position_seconds")
    private Integer positionSeconds;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PlaybackStatus status;

    @Column(name = "progress_updated_at")
    private LocalDateTime progressUpdatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
