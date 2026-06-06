package com.project.youtlix.contentlibrary.domain.service;

import com.project.youtlix.contentlibrary.domain.model.*;

/** Factory creating content aggregate roots for PU8. */
public class ContentFactory {
    /** Creates a movie aggregate. */
    public Movie createMovie(Metadata metadata, Duration duration, VideoFile file) {
        Movie movie = new Movie(ContentId.newId(), metadata, duration, file);
        movie.publish();
        return movie;
    }

    /** Creates a series aggregate. */
    public Series createSeries(Metadata metadata) {
        Series series = new Series(ContentId.newId(), metadata);
        series.publish();
        return series;
    }
}
