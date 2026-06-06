package com.project.youtlix.recommendation.domain.model.event;

import com.project.youtlix.common.domain.DomainEvent;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import java.time.Instant;

/** Domain event published after content is added to a watchlist. */
public record AddedToWatchlist(ViewerId viewerId, ContentId contentId, Instant occurredOn) implements DomainEvent {}
