package com.project.youtlix.videoplayback.application.port.in;

import com.project.youtlix.videoplayback.domain.model.ViewerId;
import java.util.List;

/** Open Host Service exposing watch activity signals. */
public interface WatchActivityApi {
    /** Returns watch activity for a viewer. */
    List<WatchActivity> watchedBy(ViewerId viewerId);
}
