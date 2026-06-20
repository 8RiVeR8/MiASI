package com.project.youtlix.recommendation.infrastructure.out.persistence;

import com.project.youtlix.recommendation.application.port.out.RatingRepository;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.Rating;
import com.project.youtlix.recommendation.domain.model.RatingId;
import com.project.youtlix.recommendation.domain.model.StarRating;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Driven adapter for recommendation ratings stored in Supabase Postgres.
 */
@Repository
public class SupabaseRatingRepository implements RatingRepository {

    private final JdbcTemplate jdbcTemplate;

    /** Creates rating persistence adapter. */
    public SupabaseRatingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Rating rating) {
        jdbcTemplate.update("""
                insert into recommendation.ratings(id, viewer_id, content_id, stars, rated_at)
                values (?, ?, ?, ?, ?)
                on conflict (viewer_id, content_id) do update set
                    stars = excluded.stars,
                    rated_at = excluded.rated_at
                """,
                rating.id().value(),
                rating.viewerId().value(),
                rating.contentId().value(),
                (short) rating.stars().value(),
                java.sql.Timestamp.from(rating.ratedAt())
        );
    }

    @Override
    public Optional<Rating> ofViewerAndContent(ViewerId viewerId, ContentId contentId) {
        return jdbcTemplate.query(
                "select * from recommendation.ratings where viewer_id = ? and content_id = ?",
                rs -> rs.next() ? Optional.of(toDomain(rs)) : Optional.empty(),
                viewerId.value(),
                contentId.value()
        );
    }

    @Override
    public List<Rating> ofViewer(ViewerId viewerId) {
        return jdbcTemplate.query(
                "select * from recommendation.ratings where viewer_id = ? order by rated_at desc",
                (rs, rowNumber) -> toDomain(rs),
                viewerId.value()
        );
    }

    private Rating toDomain(ResultSet row) throws SQLException {
        return new Rating(
                new RatingId(row.getObject("id", UUID.class)),
                new ViewerId(row.getObject("viewer_id", UUID.class)),
                new ContentId(row.getObject("content_id", UUID.class)),
                new StarRating(row.getShort("stars")),
                instant(row, "rated_at"),
                false
        );
    }

    private Instant instant(ResultSet row, String column) throws SQLException {
        return row.getTimestamp(column).toInstant();
    }
}
