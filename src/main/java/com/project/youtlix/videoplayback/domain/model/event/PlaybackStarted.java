package com.project.youtlix.videoplayback.domain.model.event;

import com.project.youtlix.videoplayback.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.DomainEvent;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import com.project.youtlix.videoplayback.domain.model.ViewerId;

import java.time.Instant;

/**
 * Event emitted when playback starts or resumes.
 */
public record PlaybackStarted(
        ViewerId viewerId,
        ContentId contentId,
        PlaybackProgress resumedFrom,
        Instant occurredOn
) implements DomainEvent {
}
