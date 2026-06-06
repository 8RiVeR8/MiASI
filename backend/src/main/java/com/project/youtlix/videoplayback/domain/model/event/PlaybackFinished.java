package com.project.youtlix.videoplayback.domain.model.event;

import com.project.youtlix.common.domain.DomainEvent;
import com.project.youtlix.videoplayback.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.ViewerId;
import java.time.Instant;

/** Domain event published when playback is completed. */
public record PlaybackFinished(ViewerId viewerId, ContentId contentId, Instant occurredOn) implements DomainEvent {}
