package com.project.youtlix.integration.common;

import com.project.youtlix.common.infrastructure.out.event.InMemoryDomainEventBus;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.event.ContentRemoved;
import com.project.youtlix.integration.support.IntegrationTestSupport;
import com.project.youtlix.testsupport.annotation.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class SpringDomainEventPublisherIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private InMemoryDomainEventBus eventBus;

    @Test
    void publishesContentRemovedToRegisteredHandlers() {
        ContentRemoved event = new ContentRemoved(new ContentId(UUID.randomUUID()), Instant.now());

        eventBus.publish(event);

        assertThat(eventBus.publishedEvents()).contains(event);
    }
}
