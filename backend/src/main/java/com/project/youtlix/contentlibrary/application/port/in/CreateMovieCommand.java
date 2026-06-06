package com.project.youtlix.contentlibrary.application.port.in;

import com.project.youtlix.contentlibrary.domain.model.*;

/** Command for PU8 adding a new movie to the library. */
public record CreateMovieCommand(Metadata metadata, Duration duration, VideoFile videoFile) {}
