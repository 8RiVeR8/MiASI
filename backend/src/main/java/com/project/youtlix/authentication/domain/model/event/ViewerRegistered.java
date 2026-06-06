package com.project.youtlix.authentication.domain.model.event;

import com.project.youtlix.authentication.domain.model.Email;
import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.common.domain.DomainEvent;
import java.time.Instant;

/** Domain event published when a new viewer account is registered. */
public record ViewerRegistered(ViewerId viewerId, Email email, Instant occurredOn) implements DomainEvent {}
