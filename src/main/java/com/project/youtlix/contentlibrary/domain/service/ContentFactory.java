package com.project.youtlix.contentlibrary.domain.service;

import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Duration;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Movie;
import com.project.youtlix.contentlibrary.domain.model.Series;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;

/**
 * Factory responsible for creating content aggregate roots.
 */
public class ContentFactory {

    /**
     * Creates a movie aggregate.
     */
    public Movie createMovie(Metadata metadata, Duration duration, VideoFile videoFile) {
        Movie movie = new Movie(ContentId.newId(), metadata, duration, videoFile);
        movie.publish();
        return movie;
    }

    /**
     * Creates a series aggregate.
     */
    public Series createSeries(Metadata metadata) {
        Series series = new Series(ContentId.newId(), metadata);
        series.publish();
        return series;
    }
}
