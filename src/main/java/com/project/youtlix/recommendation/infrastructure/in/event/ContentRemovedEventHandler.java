package com.project.youtlix.recommendation.infrastructure.in.event;

import com.project.youtlix.common.application.port.out.DomainEventHandler;
import com.project.youtlix.contentlibrary.domain.model.event.ContentRemoved;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.domain.model.ContentId;
import org.springframework.stereotype.Component;

/**
 * In-process event adapter reacting to content removals from the library.
 */
@Component
public class ContentRemovedEventHandler implements DomainEventHandler {

    private final RecommendationUseCase useCase;

    public ContentRemovedEventHandler(RecommendationUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public boolean supports(Object event) {
        return event instanceof ContentRemoved;
    }

    @Override
    public void handle(Object event) {
        ContentRemoved removed = (ContentRemoved) event;
        useCase.removeFromWatchlists(new ContentId(removed.contentId().value()));
    }
}
