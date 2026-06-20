package com.project.youtlix.contentlibrary.domain.model;

import com.project.youtlix.contentlibrary.domain.model.event.ContentModified;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Series aggregate root composed of seasons and episodes.
 */
public class Series extends Content {

    private final List<Season> seasons = new ArrayList<>();

    /**
     * Creates a series aggregate.
     */
    public Series(ContentId id, Metadata metadata) {
        super(id, metadata);
    }

    /**
     * Recreates a series aggregate from persistence.
     */
    public Series(ContentId id, Metadata metadata, boolean available, boolean recordAddedEvent) {
        super(id, metadata, available, recordAddedEvent);
    }

    /**
     * Adds a season to the series aggregate.
     *
     * @param season season entity
     */
    public void addSeason(Season season) {
        Objects.requireNonNull(season, "season must not be null");
        if (seasons.stream().anyMatch(existing -> existing.number() == season.number())) {
            throw new IllegalArgumentException("season number already exists in series");
        }
        seasons.add(season);
    }

    /**
     * Finds a season inside this aggregate.
     *
     * @param seasonId season id
     * @return matching season if present
     */
    public Optional<Season> seasonById(SeasonId seasonId) {
        return seasons.stream().filter(season -> season.id().equals(seasonId)).findFirst();
    }

    /**
     * Updates season details inside this aggregate.
     */
    public void updateSeason(SeasonId seasonId, int number, String title) {
        Season season = seasonById(seasonId)
                .orElseThrow(() -> new IllegalArgumentException("season not found: " + seasonId.value()));
        if (seasons.stream().anyMatch(existing ->
                !existing.id().equals(seasonId) && existing.number() == number)) {
            throw new IllegalArgumentException("season number already exists in series");
        }
        season.updateDetails(number, title);
        recordEvent(new ContentModified(id(), Instant.now()));
    }

    /**
     * Updates an episode inside one of this series seasons.
     */
    public void updateEpisode(
            SeasonId seasonId,
            EpisodeId episodeId,
            int number,
            String title,
            Duration duration,
            VideoFile videoFile
    ) {
        Season season = seasonById(seasonId)
                .orElseThrow(() -> new IllegalArgumentException("season not found: " + seasonId.value()));
        Episode episode = season.episodeById(episodeId)
                .orElseThrow(() -> new IllegalArgumentException("episode not found: " + episodeId.value()));
        if (season.episodes().stream().anyMatch(existing ->
                !existing.id().equals(episodeId) && existing.number() == number)) {
            throw new IllegalArgumentException("episode number already exists in season");
        }
        episode.updateDetails(number, title, duration, videoFile);
        recordEvent(new ContentModified(id(), Instant.now()));
    }

    /**
     * Counts episodes in all seasons.
     *
     * @return episode count
     */
    public int episodeCount() {
        return seasons.stream().mapToInt(season -> season.episodes().size()).sum();
    }

    public List<Season> seasons() {
        return Collections.unmodifiableList(seasons);
    }
}
