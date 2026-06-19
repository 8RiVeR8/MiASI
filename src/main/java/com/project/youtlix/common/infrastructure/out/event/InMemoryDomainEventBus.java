package com.project.youtlix.common.infrastructure.out.event;

import com.project.youtlix.common.application.port.out.DomainEventHandler;
import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import org.springframework.beans.factory.ObjectProvider;
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
    private final ObjectProvider<DomainEventHandler> handlers;

    public InMemoryDomainEventBus(ObjectProvider<DomainEventHandler> handlers) {
        this.handlers = handlers;
    }

    /**
     * Stores a domain event in memory so synchronous handlers can be added later.
     *
     * @param event event produced by a module
     */
    @Override
    public void publish(Object event) {
        publishedEvents.add(event);
        handlers.orderedStream()
                .filter(handler -> handler.supports(event))
                .forEach(handler -> handler.handle(event));
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
