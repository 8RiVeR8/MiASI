package com.project.youtlix.videoplayback.domain.service;

import com.project.youtlix.videoplayback.domain.model.Playback;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;

/** Domain service handling PU14 playback start, progress and finish rules. */
public class PlaybackService {
    /** Starts or resumes playback based on existing progress. */
    public void play(Playback playback) { playback.start(playback.isResumable() ? playback.progress() : PlaybackProgress.start()); }
    /** Saves playback progress. */
    public void saveProgress(Playback playback, PlaybackProgress progress) { playback.updateProgress(progress); }
    /** Finishes playback. */
    public void finish(Playback playback) { playback.complete(); }
}
