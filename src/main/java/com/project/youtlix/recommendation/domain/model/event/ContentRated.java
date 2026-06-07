package com.project.youtlix.recommendation.domain.model.event;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.common.domain.model.DomainEvent;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.StarRating;

import java.time.Instant;

/**
 * Event emitted when a viewer rates content.
 */
public record ContentRated(
        ViewerId viewerId,
        ContentId contentId,
        StarRating stars,
        Instant occurredOn
) implements DomainEvent {
}
