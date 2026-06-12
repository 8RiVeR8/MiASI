package com.project.youtlix.videoplayback.domain.model;

import java.time.Instant;

/**
 * Event contract for playback domain events.
 */
public interface DomainEvent {

    /** Returns the time when the domain event occurred. */
    Instant occurredOn();
}
