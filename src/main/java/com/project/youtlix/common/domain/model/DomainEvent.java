package com.project.youtlix.common.domain.model;

import java.time.Instant;

/**
 * Marker contract for events emitted by aggregates in every bounded context.
 */
public interface DomainEvent {

    /**
     * Returns the time when the domain event occurred.
     *
     * @return event occurrence time
     */
    Instant occurredOn();
}
