package com.project.youtlix.common.domain;

import java.time.Instant;

/** Marker contract for domain events published inside the modular monolith. */
public interface DomainEvent {
    /** Returns the moment when the event occurred. */
    Instant occurredOn();
}
