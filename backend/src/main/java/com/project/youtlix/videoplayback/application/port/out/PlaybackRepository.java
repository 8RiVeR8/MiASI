package com.project.youtlix.videoplayback.application.port.out;

import com.project.youtlix.videoplayback.domain.model.*;
import java.util.List;
import java.util.Optional;

/** Output port for storing playback history. */
public interface PlaybackRepository {
    void save(Playback playback);
    Optional<Playback> ofId(PlaybackId id);
    Optional<Playback> ofViewerAndContent(ViewerId viewerId, ContentId contentId);
    List<Playback> ofViewer(ViewerId viewerId);
}
