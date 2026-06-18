package com.project.youtlix.videoplayback.application.port.in;

import com.project.youtlix.videoplayback.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import com.project.youtlix.videoplayback.domain.model.ViewerId;

/**
 * Inbound port for PU14 playback operations.
 */
public interface PlaybackUseCase {

    /** Starts or resumes playback and returns stream metadata for the client. */
    StartedPlayback play(ViewerId viewerId, ContentId contentId);

    /** Saves playback progress for a viewer and content item. */
    void saveProgress(ViewerId viewerId, ContentId contentId, PlaybackProgress progress);

    /** Marks playback as completed for a viewer and content item. */
    void finish(ViewerId viewerId, ContentId contentId);
}
