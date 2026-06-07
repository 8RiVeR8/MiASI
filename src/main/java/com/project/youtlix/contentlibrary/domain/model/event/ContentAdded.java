package com.project.youtlix.contentlibrary.domain.model.event;

import com.project.youtlix.common.domain.model.DomainEvent;
import com.project.youtlix.contentlibrary.domain.model.ContentId;

import java.time.Instant;

/**
 * Event emitted when content is added to the library.
 */
public record ContentAdded(ContentId contentId, String title, Instant occurredOn) implements DomainEvent {
}
