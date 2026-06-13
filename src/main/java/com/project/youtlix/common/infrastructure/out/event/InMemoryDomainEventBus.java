package com.project.youtlix.common.infrastructure.out.event;

import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In-process event bus used by the modular monolith.
 *
 * <p>This is intentionally simple: it keeps the boundary for future handlers
 * without introducing a broker or an outbox.</p>
 */
@Component
public class InMemoryDomainEventBus implements DomainEventPublisher {

    private final List<Object> publishedEvents = new ArrayList<>();

    /**
     * Stores a domain event in memory so synchronous handlers can be added later.
     *
     * @param event event produced by a module
     */
    @Override
    public void publish(Object event) {
        publishedEvents.add(event);
    }

    /**
     * Returns immutable snapshot of events published in this process.
     *
     * @return published domain events
     */
    public List<Object> publishedEvents() {
        return Collections.unmodifiableList(publishedEvents);
    }
}
