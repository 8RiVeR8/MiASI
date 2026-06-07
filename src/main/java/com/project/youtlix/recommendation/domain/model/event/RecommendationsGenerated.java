package com.project.youtlix.recommendation.domain.model.event;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.common.domain.model.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when recommendations are generated.
 */
public record RecommendationsGenerated(ViewerId viewerId, int size, Instant occurredOn) implements DomainEvent {
}
