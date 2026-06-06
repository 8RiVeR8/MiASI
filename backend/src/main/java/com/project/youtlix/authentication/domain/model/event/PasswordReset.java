package com.project.youtlix.authentication.domain.model.event;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.common.domain.DomainEvent;
import java.time.Instant;

/** Domain event published after a viewer password is reset. */
public record PasswordReset(ViewerId viewerId, Instant occurredOn) implements DomainEvent {}
