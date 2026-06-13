package com.project.youtlix.recommendation.domain.model;

import java.time.Instant;

/**
 * Event contract for recommendation domain events.
 */
public interface DomainEvent {

    /** Returns the time when the domain event occurred. */
    Instant occurredOn();
}
