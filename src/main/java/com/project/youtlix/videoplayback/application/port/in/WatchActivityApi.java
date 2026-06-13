package com.project.youtlix.videoplayback.application.port.in;

import com.project.youtlix.videoplayback.domain.model.ViewerId;
import com.project.youtlix.videoplayback.domain.model.WatchActivity;

import java.util.List;

/**
 * Open Host Service exposing playback activity to recommendation context.
 */
public interface WatchActivityApi {

    /** Returns activity signals for a viewer. */
    List<WatchActivity> watchedBy(ViewerId viewerId);
}
