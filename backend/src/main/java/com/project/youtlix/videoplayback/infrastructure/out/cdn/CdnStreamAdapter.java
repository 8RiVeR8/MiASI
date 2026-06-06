package com.project.youtlix.videoplayback.infrastructure.out.cdn;

import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.videoplayback.application.port.out.VideoStreamPort;
import com.project.youtlix.videoplayback.domain.model.VideoStream;
import org.springframework.stereotype.Component;

/** Placeholder CDN adapter for opening a video stream. */
@Component
public class CdnStreamAdapter implements VideoStreamPort {
    @Override public VideoStream open(VideoFile file) {
        String language = file.languages().isEmpty() ? "default" : file.languages().getFirst();
        return new VideoStream(file.uri(), language);
    }
}
