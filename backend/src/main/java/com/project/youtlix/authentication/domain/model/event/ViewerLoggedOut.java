package com.project.youtlix.authentication.domain.model.event;

import com.project.youtlix.authentication.domain.model.SessionId;
import com.project.youtlix.common.domain.DomainEvent;
import java.time.Instant;

/** Domain event published when a viewer logs out. */
public record ViewerLoggedOut(SessionId sessionId, Instant occurredOn) implements DomainEvent {}
