package com.project.youtlix.recommendation.application.port.out;

import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.ViewerId;

/** Watch activity consumed by recommendation from playback context. */
public record WatchActivity(ViewerId viewerId, ContentId contentId, int positionSeconds, boolean completed) {}
