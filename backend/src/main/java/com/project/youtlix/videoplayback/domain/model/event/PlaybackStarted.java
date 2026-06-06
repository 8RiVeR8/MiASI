package com.project.youtlix.videoplayback.domain.model.event;

import com.project.youtlix.common.domain.DomainEvent;
import com.project.youtlix.videoplayback.domain.model.*;
import java.time.Instant;

/** Domain event published when playback starts or resumes. */
public record PlaybackStarted(ViewerId viewerId, ContentId contentId, PlaybackProgress resumedFrom, Instant occurredOn) implements DomainEvent {}
