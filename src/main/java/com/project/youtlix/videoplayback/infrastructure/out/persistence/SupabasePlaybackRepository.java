package com.project.youtlix.videoplayback.infrastructure.out.persistence;

import com.project.youtlix.videoplayback.application.port.out.PlaybackRepository;
import com.project.youtlix.videoplayback.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.Playback;
import com.project.youtlix.videoplayback.domain.model.PlaybackId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import com.project.youtlix.videoplayback.domain.model.PlaybackStatus;
import com.project.youtlix.videoplayback.domain.model.ViewerId;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Driven adapter for Supabase Postgres playback schema.
 */
@Repository
public class SupabasePlaybackRepository implements PlaybackRepository {

    private final JdbcTemplate jdbcTemplate;

    /** Creates the playback persistence adapter. */
    public SupabasePlaybackRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Playback playback) {
        jdbcTemplate.update("""
                insert into playback.playbacks(
                    id, viewer_id, playable_id, playable_type,
                    position_seconds, progress_updated_at, status
                )
                values (?, ?, ?, 'MOVIE', ?, ?, ?::playback.playback_status)
                on conflict (viewer_id, playable_type, playable_id) do update set
                    position_seconds = excluded.position_seconds,
                    progress_updated_at = excluded.progress_updated_at,
                    status = excluded.status
                """,
                playback.id().value(),
                playback.viewerId().value(),
                playback.contentId().value(),
                playback.progress().positionSeconds(),
                playback.progress().updatedAt(),
                playback.status().name()
        );
    }

    @Override
    public Optional<Playback> ofId(PlaybackId id) {
        return jdbcTemplate.query(
                "select * from playback.playbacks where id = ?",
                rs -> rs.next() ? Optional.of(toDomain(rs)) : Optional.empty(),
                id.value()
        );
    }

    @Override
    public Optional<Playback> ofViewerAndContent(ViewerId viewerId, ContentId contentId) {
        return jdbcTemplate.query(
                "select * from playback.playbacks where viewer_id = ? and playable_id = ?",
                rs -> rs.next() ? Optional.of(toDomain(rs)) : Optional.empty(),
                viewerId.value(),
                contentId.value()
        );
    }

    @Override
    public List<Playback> ofViewer(ViewerId viewerId) {
        return jdbcTemplate.query(
                "select * from playback.playbacks where viewer_id = ?",
                (rs, rowNumber) -> toDomain(rs),
                viewerId.value()
        );
    }

    private Playback toDomain(ResultSet row) throws SQLException {
        return new Playback(
                new PlaybackId(row.getObject("id", UUID.class)),
                new ViewerId(row.getObject("viewer_id", UUID.class)),
                new ContentId(row.getObject("playable_id", UUID.class)),
                new PlaybackProgress(row.getInt("position_seconds"), progressUpdatedAt(row)),
                PlaybackStatus.valueOf(row.getString("status"))
        );
    }

    private Instant progressUpdatedAt(ResultSet row) throws SQLException {
        return row.getTimestamp("progress_updated_at").toInstant();
    }
}
