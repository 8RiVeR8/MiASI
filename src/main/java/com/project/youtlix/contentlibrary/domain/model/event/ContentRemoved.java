package com.project.youtlix.contentlibrary.domain.model.event;

import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when content is removed or withdrawn from public catalog.
 */
public record ContentRemoved(ContentId contentId, Instant occurredOn) implements DomainEvent {
}
