package com.project.youtlix.videoplayback.application.port.in;

import com.project.youtlix.videoplayback.domain.model.*;

/** Published watch activity signal exposed to recommendation. */
public record WatchActivity(ViewerId viewerId, ContentId contentId, PlaybackProgress progress, boolean completed) {}
