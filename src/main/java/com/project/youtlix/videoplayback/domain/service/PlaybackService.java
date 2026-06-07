package com.project.youtlix.videoplayback.domain.service;

import com.project.youtlix.videoplayback.domain.model.Playback;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;

/**
 * Domain service containing playback state transition rules.
 */
public class PlaybackService {

    /**
     * Starts playback from saved progress when possible, otherwise from the beginning.
     *
     * @param playback playback aggregate
     */
    public void play(Playback playback) {
        PlaybackProgress progress = playback.isResumable() ? playback.progress() : PlaybackProgress.start();
        playback.start(progress);
    }
}
