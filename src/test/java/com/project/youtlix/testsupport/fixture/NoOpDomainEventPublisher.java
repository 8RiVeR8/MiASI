package com.project.youtlix.testsupport.fixture;

import com.project.youtlix.common.application.port.out.DomainEventPublisher;

public final class NoOpDomainEventPublisher implements DomainEventPublisher {

    @Override
    public void publish(Object event) {
    }
}
