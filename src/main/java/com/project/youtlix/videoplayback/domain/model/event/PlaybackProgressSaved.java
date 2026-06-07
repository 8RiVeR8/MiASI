package com.project.youtlix.videoplayback.domain.model.event;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.common.domain.model.DomainEvent;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;

import java.time.Instant;

/**
 * Event emitted when playback progress is saved.
 */
public record PlaybackProgressSaved(
        ViewerId viewerId,
        ContentId contentId,
        PlaybackProgress progress,
        Instant occurredOn
) implements DomainEvent {
}
