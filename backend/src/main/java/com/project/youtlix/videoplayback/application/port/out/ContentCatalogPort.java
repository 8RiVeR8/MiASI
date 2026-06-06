package com.project.youtlix.videoplayback.application.port.out;

import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.videoplayback.domain.model.ContentId;

/** Output port to content catalog technical video data. */
public interface ContentCatalogPort {
    VideoFile videoFileOf(ContentId contentId);
}
