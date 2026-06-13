package com.project.youtlix.contentlibrary.domain.model.event;

import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when content metadata changes.
 */
public record ContentModified(ContentId contentId, Instant occurredOn) implements DomainEvent {
}
