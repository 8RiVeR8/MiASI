package com.project.youtlix.videoplayback.application.port.in;

import com.project.youtlix.videoplayback.domain.model.PlaybackId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import com.project.youtlix.videoplayback.domain.model.VideoStream;

/** Result returned when a viewer starts or resumes playback. */
public record PlaybackSession(PlaybackId playbackId, VideoStream stream, PlaybackProgress startPosition) {}
