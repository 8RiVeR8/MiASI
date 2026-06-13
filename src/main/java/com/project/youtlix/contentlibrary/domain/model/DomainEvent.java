package com.project.youtlix.contentlibrary.domain.model;

import java.time.Instant;

/**
 * Event contract for content library domain events.
 */
public interface DomainEvent {

    /** Returns the time when the domain event occurred. */
    Instant occurredOn();
}
