package com.project.youtlix.common.application.port.out;

import java.util.Collection;

/**
 * Technical outbound port used by application services to publish domain events.
 *
 * <p>The event type is intentionally not shared because every bounded context
 * defines its own DomainEvent contract.</p>
 */
public interface DomainEventPublisher {

    /**
     * Publishes one domain event.
     *
     * @param event event produced by an aggregate
     */
    void publish(Object event);

    /**
     * Publishes all events from an aggregate.
     *
     * @param events events produced during one use case
     */
    default void publishAll(Collection<?> events) {
        events.forEach(this::publish);
    }
}
