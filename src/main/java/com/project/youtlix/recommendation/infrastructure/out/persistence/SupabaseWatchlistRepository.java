package com.project.youtlix.recommendation.infrastructure.out.persistence;

import com.project.youtlix.recommendation.application.port.out.WatchlistRepository;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.recommendation.domain.model.Watchlist;
import com.project.youtlix.recommendation.domain.model.WatchlistId;
import com.project.youtlix.recommendation.domain.model.WatchlistItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Driven adapter for recommendation watchlists stored in Supabase Postgres.
 */
@Repository
public class SupabaseWatchlistRepository implements WatchlistRepository {

    private final JdbcTemplate jdbcTemplate;

    /** Creates watchlist persistence adapter. */
    public SupabaseWatchlistRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void save(Watchlist watchlist) {
        jdbcTemplate.update("""
                insert into recommendation.watchlists(id, viewer_id)
                values (?, ?)
                on conflict (viewer_id) do update set updated_at = now()
                """,
                watchlist.id().value(),
                watchlist.viewerId().value()
        );
        UUID persistedId = jdbcTemplate.queryForObject(
                "select id from recommendation.watchlists where viewer_id = ?",
                UUID.class,
                watchlist.viewerId().value()
        );
        jdbcTemplate.update("delete from recommendation.watchlist_items where watchlist_id = ?", persistedId);
        watchlist.items().forEach(item -> jdbcTemplate.update("""
                insert into recommendation.watchlist_items(id, watchlist_id, content_id, added_on)
                values (?, ?, ?, ?)
                """,
                UUID.randomUUID(),
                persistedId,
                item.contentId().value(),
                item.addedOn()
        ));
    }

    @Override
    public Optional<Watchlist> ofViewer(ViewerId viewerId) {
        return jdbcTemplate.query(
                "select id, viewer_id from recommendation.watchlists where viewer_id = ?",
                rs -> rs.next() ? Optional.of(toDomain(rs)) : Optional.empty(),
                viewerId.value()
        );
    }

    @Override
    public void removeFromWatchlists(ContentId contentId) {
        jdbcTemplate.update(
                "delete from recommendation.watchlist_items where content_id = ?",
                contentId.value()
        );
    }

    private Watchlist toDomain(ResultSet row) throws SQLException {
        UUID watchlistId = row.getObject("id", UUID.class);
        List<WatchlistItem> items = jdbcTemplate.query(
                "select content_id, added_on from recommendation.watchlist_items where watchlist_id = ? order by added_on",
                (itemRow, rowNumber) -> new WatchlistItem(
                        new ContentId(itemRow.getObject("content_id", UUID.class)),
                        instant(itemRow, "added_on")
                ),
                watchlistId
        );
        return new Watchlist(
                new WatchlistId(watchlistId),
                new ViewerId(row.getObject("viewer_id", UUID.class)),
                items
        );
    }

    private Instant instant(ResultSet row, String column) throws SQLException {
        return row.getTimestamp(column).toInstant();
    }
}
