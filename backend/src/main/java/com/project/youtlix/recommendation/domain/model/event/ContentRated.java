package com.project.youtlix.recommendation.domain.model.event;

import com.project.youtlix.common.domain.DomainEvent;
import com.project.youtlix.recommendation.domain.model.*;
import java.time.Instant;

/** Domain event published after content is rated. */
public record ContentRated(ViewerId viewerId, ContentId contentId, StarRating stars, Instant occurredOn) implements DomainEvent {}
