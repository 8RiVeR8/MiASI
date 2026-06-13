package com.project.youtlix.videoplayback.application.port.out;

import com.project.youtlix.videoplayback.domain.model.VideoStream;

/**
 * Outbound port responsible for opening a playable stream from a video file.
 */
public interface VideoStreamPort {

    /** Opens a stream from technical video file data. */
    VideoStream open(VideoFile file);
}
