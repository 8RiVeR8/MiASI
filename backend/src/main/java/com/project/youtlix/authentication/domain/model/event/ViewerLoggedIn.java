package com.project.youtlix.authentication.domain.model.event;

import com.project.youtlix.authentication.domain.model.SessionId;
import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.common.domain.DomainEvent;
import java.time.Instant;

/** Domain event published when a viewer session is created. */
public record ViewerLoggedIn(ViewerId viewerId, SessionId sessionId, Instant occurredOn) implements DomainEvent {}
