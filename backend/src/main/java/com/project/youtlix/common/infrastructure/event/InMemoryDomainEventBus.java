package com.project.youtlix.common.infrastructure.event;

import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import com.project.youtlix.common.domain.DomainEvent;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** In-process event bus planned for the modular monolith. */
@Component
public class InMemoryDomainEventBus implements DomainEventPublisher {
    private final List<DomainEvent> publishedEvents = new CopyOnWriteArrayList<>();

    @Override public void publish(DomainEvent event) { publishedEvents.add(event); }
    /** Returns a snapshot of events published in this process. */
    public List<DomainEvent> publishedEvents() { return List.copyOf(publishedEvents); }
    /** Clears captured events for tests. */
    public void clear() { publishedEvents.clear(); }
}
