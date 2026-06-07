package com.project.youtlix.videoplayback.domain.model.event;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.common.domain.model.DomainEvent;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;

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
