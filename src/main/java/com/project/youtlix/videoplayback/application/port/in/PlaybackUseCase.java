package com.project.youtlix.videoplayback.application.port.in;

import com.project.youtlix.videoplayback.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.PlaybackId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import com.project.youtlix.videoplayback.domain.model.ViewerId;
import com.project.youtlix.videoplayback.domain.model.VideoStream;

/**
 * Inbound port for PU14 playback operations.
 */
public interface PlaybackUseCase {

    /** Starts or resumes playback and returns an opened stream. */
    VideoStream play(ViewerId viewerId, ContentId contentId);

    /** Saves playback progress. */
    void saveProgress(PlaybackId playbackId, PlaybackProgress progress);

    /** Marks playback as completed. */
    void finish(PlaybackId playbackId);
}
