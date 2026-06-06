package com.project.youtlix.videoplayback.domain.model.event;

import com.project.youtlix.common.domain.DomainEvent;
import com.project.youtlix.videoplayback.domain.model.*;
import java.time.Instant;

/** Domain event published when playback progress is saved. */
public record PlaybackProgressSaved(ViewerId viewerId, ContentId contentId, PlaybackProgress progress, Instant occurredOn) implements DomainEvent {}
