package com.project.youtlix.videoplayback.application.port.out;

import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.videoplayback.domain.model.VideoStream;

/** Output port to CDN or video storage. */
public interface VideoStreamPort {
    VideoStream open(VideoFile file);
}
