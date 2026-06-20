package com.project.youtlix.contentlibrary.infrastructure.out.persistence;

import com.project.youtlix.contentlibrary.application.port.in.ResolvedPlayable;
import com.project.youtlix.contentlibrary.application.port.out.ContentRepository;
import com.project.youtlix.contentlibrary.domain.model.Content;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Duration;
import com.project.youtlix.contentlibrary.domain.model.Episode;
import com.project.youtlix.contentlibrary.domain.model.EpisodeId;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Movie;
import com.project.youtlix.contentlibrary.domain.model.Page;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;
import com.project.youtlix.contentlibrary.domain.model.Season;
import com.project.youtlix.contentlibrary.domain.model.SeasonId;
import com.project.youtlix.contentlibrary.domain.model.Series;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.contentlibrary.domain.model.ContentType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Driven adapter for content aggregates stored in the Supabase library schema.
 */
@Repository
public class SupabaseContentRepository implements ContentRepository {

    private final JdbcTemplate jdbcTemplate;

    /** Creates the library persistence adapter. */
    public SupabaseContentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void save(Content content) {
        saveContentRow(content);
        saveKeywords(content.id(), content.metadata().keywords());
        if (content instanceof Movie movie) {
            deleteSeriesRow(content.id());
            saveMovieRow(movie);
        } else if (content instanceof Series series) {
            deleteMovieRow(content.id());
            saveSeries(series);
        }
    }

    @Override
    public Optional<Content> ofId(ContentId id) {
        Optional<ContentRow> row = jdbcTemplate.query(
                "select * from library.contents where id = ?",
                rs -> rs.next() ? Optional.of(toContentRow(rs)) : Optional.empty(),
                id.value()
        );
        return row.map(this::toDomain);
    }

    @Override
    public List<Content> matching(SearchCriteria criteria) {
        String phrase = normalizedLike(criteria.phrase());
        String genre = criteria.genre() == null ? null : criteria.genre().name();
        return contentRows("""
                select distinct c.*
                from library.contents c
                left join library.content_keywords k on k.content_id = c.id
                where c.available = true
                  and (cast(? as text) is null or lower(c.title) like ? or lower(k.keyword) like ?)
                  and (cast(? as text) is null or c.genre::text = ?)
                  and (cast(? as integer) is null or c.release_year >= ?)
                  and (cast(? as integer) is null or c.release_year <= ?)
                order by c.release_year desc, c.title
                """,
                phrase, phrase, phrase,
                genre, genre,
                criteria.yearFrom(), criteria.yearFrom(),
                criteria.yearTo(), criteria.yearTo()
        );
    }

    @Override
    public List<Content> byKeyword(String phrase) {
        return matching(new SearchCriteria(phrase, null, null, null));
    }

    @Override
    public List<Content> page(Page page) {
        return contentRows("""
                select *
                from library.contents
                where available = true
                order by release_year desc, title
                limit ? offset ?
                """,
                page.size(),
                (long) page.number() * page.size()
        );
    }

    @Override
    public List<ContentId> popularContent(int limit) {
        return jdbcTemplate.query(
                """
                select id
                from library.contents
                where available = true
                order by release_year desc, title
                limit ?
                """,
                (rs, rowNumber) -> new ContentId(rs.getObject("id", UUID.class)),
                Math.max(limit, 0)
        );
    }

    @Override
    public Optional<VideoFile> videoFileOf(ContentId id) {
        return movieDetails(id.value()).map(MovieDetails::videoFile);
    }

    @Override
    public Optional<ResolvedPlayable> resolvePlayable(UUID id) {
        Optional<VideoFile> movieVideo = movieDetails(id).map(MovieDetails::videoFile);
        if (movieVideo.isPresent()) {
            return Optional.of(new ResolvedPlayable(id, ResolvedPlayable.PlayableKind.MOVIE, movieVideo.get()));
        }
        return episodeVideoFile(id).map(videoFile ->
                new ResolvedPlayable(id, ResolvedPlayable.PlayableKind.EPISODE, videoFile)
        );
    }

    @Override
    public boolean isSeries(ContentId id) {
        Boolean isSeries = jdbcTemplate.query(
                "select content_type = 'SERIES' as is_series from library.contents where id = ?",
                rs -> rs.next() ? rs.getBoolean("is_series") : false,
                id.value()
        );
        return Boolean.TRUE.equals(isSeries);
    }

    private Optional<VideoFile> episodeVideoFile(UUID episodeId) {
        return jdbcTemplate.query(
                "select video_uri, video_languages from library.episodes where id = ?",
                rs -> rs.next() ? Optional.of(videoFile(rs)) : Optional.empty(),
                episodeId
        );
    }

    @Override
    @Transactional
    public void remove(ContentId id) {
        jdbcTemplate.update("delete from library.contents where id = ?", id.value());
    }

    private List<Content> contentRows(String sql, Object... parameters) {
        List<ContentRow> rows = jdbcTemplate.query(sql, (rs, rowNumber) -> toContentRow(rs), parameters);
        return rows.stream().map(this::toDomain).toList();
    }

    private ContentRow toContentRow(ResultSet row) throws SQLException {
        return new ContentRow(
                row.getObject("id", UUID.class),
                ContentType.valueOf(row.getString("content_type")),
                row.getString("title"),
                row.getString("description"),
                row.getString("thumbnail_url"),
                Genre.valueOf(row.getString("genre")),
                row.getInt("release_year"),
                row.getBoolean("available")
        );
    }

    private Content toDomain(ContentRow row) {
        ContentId contentId = new ContentId(row.id());
        Metadata metadata = new Metadata(
                row.title(),
                row.description(),
                row.contentType(),
                row.thumbnailUrl(),
                row.genre(),
                row.releaseYear(),
                keywords(row.id())
        );
        if ("MOVIE".equals(row.contentType())) {
            MovieDetails details = movieDetails(row.id())
                    .orElseThrow(() -> new IllegalStateException("movie details not found: " + row.id()));
            return new Movie(contentId, metadata, details.duration(), details.videoFile(), row.available(), false);
        }
        Series series = new Series(contentId, metadata, row.available(), false);
        seasons(row.id()).forEach(series::addSeason);
        return series;
    }

    private void saveContentRow(Content content) {
        Metadata metadata = content.metadata();
        jdbcTemplate.update("""
                insert into library.contents(
                    id, content_type, title, description, thumbnail_url, genre, release_year, available
                )
                values (?, ?::library.content_type, ?, ?, ?, ?::library.genre, ?, ?)
                on conflict (id) do update set
                    content_type = excluded.content_type,
                    title = excluded.title,
                    description = excluded.description,
                    thumbnail_url = excluded.thumbnail_url,
                    genre = excluded.genre,
                    release_year = excluded.release_year,
                    available = excluded.available
                """,
                content.id().value(),
                content instanceof Movie ? "MOVIE" : "SERIES",
                metadata.title(),
                metadata.description(),
                metadata.thumbnailUrl(),
                metadata.genre().name(),
                metadata.releaseYear(),
                content.available()
        );
    }

    private List<Keyword> keywords(UUID contentId) {
        return jdbcTemplate.query(
                "select keyword from library.content_keywords where content_id = ? order by keyword",
                (rs, rowNumber) -> new Keyword(rs.getString("keyword")),
                contentId
        );
    }

    private void saveKeywords(ContentId contentId, List<Keyword> keywords) {
        jdbcTemplate.update("delete from library.content_keywords where content_id = ?", contentId.value());
        keywords.forEach(keyword -> jdbcTemplate.update(
                "insert into library.content_keywords(content_id, keyword) values (?, ?)",
                contentId.value(),
                keyword.value()
        ));
    }

    private Optional<MovieDetails> movieDetails(UUID contentId) {
        return jdbcTemplate.query(
                "select duration_sec, video_uri, video_languages from library.movies where content_id = ?",
                rs -> rs.next() ? Optional.of(toMovieDetails(rs)) : Optional.empty(),
                contentId
        );
    }

    private MovieDetails toMovieDetails(ResultSet row) throws SQLException {
        return new MovieDetails(
                Duration.ofSeconds(row.getInt("duration_sec")),
                videoFile(row)
        );
    }

    private void saveMovieRow(Movie movie) {
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement("""
                    insert into library.movies(content_id, duration_sec, video_uri, video_languages)
                    values (?, ?, ?, ?)
                    on conflict (content_id) do update set
                        duration_sec = excluded.duration_sec,
                        video_uri = excluded.video_uri,
                        video_languages = excluded.video_languages
                    """);
            statement.setObject(1, movie.id().value());
            statement.setInt(2, movie.duration().seconds());
            statement.setString(3, movie.videoFile().uri());
            statement.setArray(4, connection.createArrayOf("text", movie.videoFile().languages().toArray()));
            return statement;
        });
    }

    private void saveSeries(Series series) {
        jdbcTemplate.update(
                "insert into library.series(content_id) values (?) on conflict (content_id) do nothing",
                series.id().value()
        );
        jdbcTemplate.update("delete from library.seasons where series_id = ?", series.id().value());
        series.seasons().forEach(season -> saveSeason(series.id(), season));
    }

    private void saveSeason(ContentId seriesId, Season season) {
        jdbcTemplate.update(
                "insert into library.seasons(id, series_id, season_number, title) values (?, ?, ?, ?)",
                season.id().value(),
                seriesId.value(),
                season.number(),
                season.title()
        );
        season.episodes().forEach(episode -> saveEpisode(season.id().value(), episode));
    }

    private void saveEpisode(UUID seasonId, Episode episode) {
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement("""
                    insert into library.episodes(
                        id, season_id, episode_number, title, duration_sec, video_uri, video_languages
                    )
                    values (?, ?, ?, ?, ?, ?, ?)
                    """);
            statement.setObject(1, episode.id().value());
            statement.setObject(2, seasonId);
            statement.setInt(3, episode.number());
            statement.setString(4, episode.title());
            statement.setInt(5, episode.duration().seconds());
            statement.setString(6, episode.videoFile().uri());
            statement.setArray(7, connection.createArrayOf("text", episode.videoFile().languages().toArray()));
            return statement;
        });
    }

    private List<Season> seasons(UUID seriesId) {
        List<SeasonRow> rows = jdbcTemplate.query(
                """
                select id, season_number, title
                from library.seasons
                where series_id = ?
                order by season_number
                """,
                (rs, rowNumber) -> new SeasonRow(
                        rs.getObject("id", UUID.class),
                        rs.getInt("season_number"),
                        rs.getString("title")
                ),
                seriesId
        );
        return rows.stream().map(this::toSeason).toList();
    }

    private Season toSeason(SeasonRow row) {
        Season season = new Season(new SeasonId(row.id()), row.number(), row.title());
        episodes(row.id()).forEach(season::addEpisode);
        return season;
    }

    private List<Episode> episodes(UUID seasonId) {
        return jdbcTemplate.query(
                """
                select id, episode_number, title, duration_sec, video_uri, video_languages
                from library.episodes
                where season_id = ?
                order by episode_number
                """,
                (rs, rowNumber) -> new Episode(
                        new EpisodeId(rs.getObject("id", UUID.class)),
                        rs.getInt("episode_number"),
                        rs.getString("title"),
                        Duration.ofSeconds(rs.getInt("duration_sec")),
                        videoFile(rs)
                ),
                seasonId
        );
    }

    private VideoFile videoFile(ResultSet row) throws SQLException {
        return new VideoFile(row.getString("video_uri"), readTextArray(row.getArray("video_languages")));
    }

    private List<String> readTextArray(Array array) throws SQLException {
        if (array == null) {
            return List.of();
        }
        Object value = array.getArray();
        if (value instanceof String[] strings) {
            return List.of(strings);
        }
        if (value instanceof Object[] objects) {
            return Arrays.stream(objects).map(String::valueOf).toList();
        }
        return List.of();
    }

    private String normalizedLike(String phrase) {
        return phrase == null || phrase.isBlank() ? null : "%" + phrase.trim().toLowerCase() + "%";
    }

    private void deleteMovieRow(ContentId contentId) {
        jdbcTemplate.update("delete from library.movies where content_id = ?", contentId.value());
    }

    private void deleteSeriesRow(ContentId contentId) {
        jdbcTemplate.update("delete from library.series where content_id = ?", contentId.value());
    }

    private record ContentRow(
            UUID id,
            ContentType contentType,
            String title,
            String description,
            String thumbnailUrl,
            Genre genre,
            int releaseYear,
            boolean available
    ) {
    }

    private record MovieDetails(Duration duration, VideoFile videoFile) {
    }

    private record SeasonRow(UUID id, int number, String title) {
    }
}
