package com.project.youtlix.contentlibrary.domain.model.event;

import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when content is added to the library.
 */
public record ContentAdded(ContentId contentId, String title, Instant occurredOn) implements DomainEvent {
}
