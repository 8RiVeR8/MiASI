package com.project.youtlix.common.application.port.out;

import com.project.youtlix.common.domain.DomainEvent;
import java.util.Collection;

/** Output port used by application services to publish domain events. */
public interface DomainEventPublisher {
    /** Publishes a single domain event. */
    void publish(DomainEvent event);

    /** Publishes all events recorded by an aggregate. */
    default void publishAll(Collection<? extends DomainEvent> events) {
        events.forEach(this::publish);
    }
}
