package com.project.youtlix.recommendation.application.port.out;

import com.project.youtlix.recommendation.domain.model.ViewerId;
import java.util.List;

/** Output port to watch activity signals from playback. */
public interface WatchActivityPort {
    List<WatchActivity> watchedBy(ViewerId viewerId);
}
