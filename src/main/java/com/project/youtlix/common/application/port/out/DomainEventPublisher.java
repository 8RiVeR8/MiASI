package com.project.youtlix.common.application.port.out;

import com.project.youtlix.common.domain.model.DomainEvent;

import java.util.Collection;

/**
 * Outbound port used by application services to publish domain events.
 */
public interface DomainEventPublisher {

    /**
     * Publishes one domain event.
     *
     * @param event event produced by an aggregate
     */
    void publish(DomainEvent event);

    /**
     * Publishes all events from an aggregate.
     *
     * @param events events produced during one use case
     */
    default void publishAll(Collection<? extends DomainEvent> events) {
        events.forEach(this::publish);
    }
}
