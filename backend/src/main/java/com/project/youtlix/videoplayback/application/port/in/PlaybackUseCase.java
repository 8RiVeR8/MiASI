package com.project.youtlix.videoplayback.application.port.in;

import com.project.youtlix.videoplayback.domain.model.*;

/** Inbound port exposing PU14 playback use cases. */
public interface PlaybackUseCase {
    /** Starts or resumes playback for viewer and content. */
    PlaybackSession play(ViewerId viewerId, ContentId contentId);
    /** Saves playback progress. */
    void saveProgress(PlaybackId playbackId, PlaybackProgress progress);
    /** Marks playback as finished. */
    void finish(PlaybackId playbackId);
}
