package com.project.youtlix.recommendation.domain.model.event;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.common.domain.model.DomainEvent;
import com.project.youtlix.contentlibrary.domain.model.ContentId;

import java.time.Instant;

/**
 * Event emitted when content is added to a watchlist.
 */
public record AddedToWatchlist(ViewerId viewerId, ContentId contentId, Instant occurredOn) implements DomainEvent {
}
