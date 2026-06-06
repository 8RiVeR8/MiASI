package com.project.youtlix.recommendation.domain.model.event;

import com.project.youtlix.common.domain.DomainEvent;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import java.time.Instant;

/** Domain event published after a recommendation list is generated. */
public record RecommendationsGenerated(ViewerId viewerId, int size, Instant occurredOn) implements DomainEvent {}
