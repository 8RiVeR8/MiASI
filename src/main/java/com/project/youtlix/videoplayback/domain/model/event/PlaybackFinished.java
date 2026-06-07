package com.project.youtlix.videoplayback.domain.model.event;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.common.domain.model.DomainEvent;
import com.project.youtlix.contentlibrary.domain.model.ContentId;

import java.time.Instant;

/**
 * Event emitted when playback is finished.
 */
public record PlaybackFinished(ViewerId viewerId, ContentId contentId, Instant occurredOn) implements DomainEvent {
}
