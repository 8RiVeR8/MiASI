package com.project.youtlix.testsupport.fixture.stub;

import com.project.youtlix.videoplayback.application.port.out.VideoFile;
import com.project.youtlix.videoplayback.application.port.out.VideoStreamPort;
import com.project.youtlix.videoplayback.domain.model.VideoStream;

public final class FakeVideoStreamPort implements VideoStreamPort {

    @Override
    public VideoStream open(VideoFile file) {
        return new VideoStream(file.uri(), file.languages().getFirst());
    }
}
