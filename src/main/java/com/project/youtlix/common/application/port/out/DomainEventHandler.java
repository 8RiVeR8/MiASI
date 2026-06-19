package com.project.youtlix.common.application.port.out;

/**
 * Synchronous in-process handler for domain events published by modules.
 */
public interface DomainEventHandler {

    /** Checks whether this handler accepts the event. */
    boolean supports(Object event);

    /** Handles a supported event. */
    void handle(Object event);
}
