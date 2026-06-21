package com.project.youtlix.unit.common.infrastructure;

import com.project.youtlix.common.infrastructure.out.event.InMemoryDomainEventBus;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.event.ContentRemoved;
import com.project.youtlix.recommendation.infrastructure.in.event.ContentRemovedEventHandler;
import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.stub.NoOpRecommendationUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
class InMemoryDomainEventBusUnitTest {

    @Test
    void publishesEventToMatchingHandler() {
        RecordingRecommendationUseCase useCase = new RecordingRecommendationUseCase();
        ContentRemovedEventHandler handler = new ContentRemovedEventHandler(useCase);
        @SuppressWarnings("unchecked")
        ObjectProvider<com.project.youtlix.common.application.port.out.DomainEventHandler> provider = mock(ObjectProvider.class);
        when(provider.orderedStream()).thenReturn(Stream.of(handler));

        InMemoryDomainEventBus bus = new InMemoryDomainEventBus(provider);
        UUID contentId = UUID.randomUUID();
        ContentRemoved event = new ContentRemoved(new ContentId(contentId), Instant.now());

        bus.publish(event);

        assertThat(bus.publishedEvents()).containsExactly(event);
        assertThat(useCase.removedContentId.value()).isEqualTo(contentId);
    }

    static class RecordingRecommendationUseCase extends NoOpRecommendationUseCase {
        com.project.youtlix.recommendation.domain.model.ContentId removedContentId;

        @Override
        public void removeFromWatchlists(com.project.youtlix.recommendation.domain.model.ContentId contentId) {
            this.removedContentId = contentId;
        }
    }
}
