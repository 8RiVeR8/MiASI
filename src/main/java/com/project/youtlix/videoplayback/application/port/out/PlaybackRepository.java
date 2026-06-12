package com.project.youtlix.videoplayback.application.port.out;

import com.project.youtlix.videoplayback.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.Playback;
import com.project.youtlix.videoplayback.domain.model.PlaybackId;
import com.project.youtlix.videoplayback.domain.model.ViewerId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for playback aggregates.
 */
public interface PlaybackRepository {

    /** Persists playback aggregate. */
    void save(Playback playback);

    /** Loads playback by id. */
    Optional<Playback> ofId(PlaybackId id);

    /** Loads playback for viewer and content. */
    Optional<Playback> ofViewerAndContent(ViewerId viewerId, ContentId contentId);

    /** Returns all playbacks for a viewer. */
    List<Playback> ofViewer(ViewerId viewerId);
}
