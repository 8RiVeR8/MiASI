package com.project.youtlix.testsupport.fixture;

import com.project.youtlix.common.application.port.out.DomainEventPublisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Captures published domain events for assertions.
 */
public final class RecordingDomainEventPublisher implements DomainEventPublisher {

    private final List<Object> events = new ArrayList<>();

    @Override
    public void publish(Object event) {
        events.add(event);
    }

    public List<Object> events() {
        return Collections.unmodifiableList(events);
    }

    public void clear() {
        events.clear();
    }
}
