package com.project.youtlix.videoplayback.domain.service;

import com.project.youtlix.videoplayback.domain.model.Playback;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;

/**
 * Domain service containing playback state transition rules.
 */
public class PlaybackService {

    /**
     * Starts playback from saved progress when possible, otherwise from the beginning.
     */
    public void play(Playback playback) {
        PlaybackProgress progress = playback.isResumable() ? playback.progress() : PlaybackProgress.start();
        playback.start(progress);
    }

    /**
     * Saves playback progress.
     */
    public void saveProgress(Playback playback, PlaybackProgress progress) {
        playback.updateProgress(progress);
    }

    /**
     * Marks playback as completed.
     */
    public void finish(Playback playback) {
        playback.complete();
    }
}
