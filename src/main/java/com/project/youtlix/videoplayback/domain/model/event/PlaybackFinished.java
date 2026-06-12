package com.project.youtlix.videoplayback.domain.model.event;

import com.project.youtlix.videoplayback.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.DomainEvent;
import com.project.youtlix.videoplayback.domain.model.ViewerId;

import java.time.Instant;

/**
 * Event emitted when playback is finished.
 */
public record PlaybackFinished(ViewerId viewerId, ContentId contentId, Instant occurredOn) implements DomainEvent {
}
