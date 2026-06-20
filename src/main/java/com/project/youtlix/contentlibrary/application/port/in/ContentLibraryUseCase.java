package com.project.youtlix.contentlibrary.application.port.in;

import com.project.youtlix.contentlibrary.domain.model.Content;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Duration;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Page;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;
import com.project.youtlix.contentlibrary.domain.model.EpisodeId;
import com.project.youtlix.contentlibrary.domain.model.SeasonId;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;

import java.util.List;

/**
 * Inbound port for PU5-PU10: browse, search, filter and manage catalog content.
 */
public interface ContentLibraryUseCase {

    /** Browses available content page by page. */
    List<Content> browse(Page page);

    /** Searches content by keyword phrase. */
    List<Content> searchByKeyword(String phrase);

    /** Filters content by provided criteria. */
    List<Content> filter(SearchCriteria criteria);

    /** Adds a movie to the library. */
    ContentId createMovie(Metadata metadata, Duration duration, VideoFile videoFile);

    /** Adds a series to the library. */
    ContentId createSeries(Metadata metadata);

    /** Adds a season to an existing series. */
    SeasonId addSeason(ContentId seriesId, int number, String title);

    /** Adds an episode to an existing series season. */
    EpisodeId addEpisode(
            ContentId seriesId,
            SeasonId seasonId,
            int number,
            String title,
            Duration duration,
            VideoFile videoFile
    );

    /** Updates an existing season inside a series. */
    void updateSeason(ContentId seriesId, SeasonId seasonId, int number, String title);

    /** Updates an existing episode inside a series season. */
    void updateEpisode(
            ContentId seriesId,
            SeasonId seasonId,
            EpisodeId episodeId,
            int number,
            String title,
            Duration duration,
            VideoFile videoFile
    );

    /** Updates metadata of existing content. */
    void updateMetadata(ContentId id, Metadata metadata);

    /** Updates an existing movie together with its playback data. */
    void updateMovie(ContentId id, Metadata metadata, Duration duration, VideoFile videoFile);

    /** Updates metadata of an existing series. */
    void updateSeriesMetadata(ContentId id, Metadata metadata);

    /** Removes content from the library. */
    void remove(ContentId id);
}
