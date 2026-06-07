package com.project.youtlix.contentlibrary.domain.model.event;

import com.project.youtlix.common.domain.model.DomainEvent;
import com.project.youtlix.contentlibrary.domain.model.ContentId;

import java.time.Instant;

/**
 * Event emitted when content is removed or withdrawn from public catalog.
 */
public record ContentRemoved(ContentId contentId, Instant occurredOn) implements DomainEvent {
}
