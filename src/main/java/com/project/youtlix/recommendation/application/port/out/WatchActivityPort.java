package com.project.youtlix.recommendation.application.port.out;

import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.videoplayback.domain.model.WatchActivity;

import java.util.List;

/**
 * Outbound port to playback activity consumed by recommendations.
 */
public interface WatchActivityPort {

    /** Returns viewer watch activity signals. */
    List<WatchActivity> watchedBy(ViewerId viewerId);
}
