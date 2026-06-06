package com.project.youtlix.contentlibrary.domain.model.event;

import com.project.youtlix.common.domain.DomainEvent;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import java.time.Instant;

/** Domain event published when content metadata is modified. */
public record ContentModified(ContentId contentId, Instant occurredOn) implements DomainEvent {}
