package com.project.youtlix.recommendation.domain.model.event;

import com.project.youtlix.recommendation.domain.model.DomainEvent;
import com.project.youtlix.recommendation.domain.model.ViewerId;

import java.time.Instant;

/**
 * Event emitted when recommendations are generated.
 */
public record RecommendationsGenerated(ViewerId viewerId, int size, Instant occurredOn) implements DomainEvent {
}
