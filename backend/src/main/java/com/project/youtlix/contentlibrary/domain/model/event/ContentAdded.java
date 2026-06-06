package com.project.youtlix.contentlibrary.domain.model.event;

import com.project.youtlix.common.domain.DomainEvent;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import java.time.Instant;

/** Domain event published when content is added. */
public record ContentAdded(ContentId contentId, String title, Instant occurredOn) implements DomainEvent {}
