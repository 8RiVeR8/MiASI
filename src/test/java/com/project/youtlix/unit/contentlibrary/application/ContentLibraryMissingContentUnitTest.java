package com.project.youtlix.unit.contentlibrary.application;

import com.project.youtlix.contentlibrary.application.service.ContentLibraryApplicationService;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.NoOpDomainEventPublisher;
import com.project.youtlix.testsupport.fixture.memory.InMemoryContentRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UnitTest
class ContentLibraryMissingContentUnitTest {

    @Test
    void metadataOfThrowsWhenContentDoesNotExist() {
        ContentLibraryApplicationService service = new ContentLibraryApplicationService(
                new InMemoryContentRepository(),
                new NoOpDomainEventPublisher()
        );

        assertThatThrownBy(() -> service.metadataOf(ContentId.newId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("content not found");
    }
}
