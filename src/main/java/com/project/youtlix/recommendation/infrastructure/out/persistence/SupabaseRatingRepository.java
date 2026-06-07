package com.project.youtlix.recommendation.infrastructure.out.persistence;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.recommendation.application.port.out.RatingRepository;
import com.project.youtlix.recommendation.domain.model.Rating;
import com.project.youtlix.recommendation.domain.model.RatingId;
import com.project.youtlix.recommendation.domain.model.StarRating;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Driven adapter for recommendation ratings stored in Supabase Postgres.
 */
@Repository
public class SupabaseRatingRepository implements RatingRepository {

    private final SpringDataRatingJpaRepository jpaRepository;

    /** Creates rating persistence adapter. */
    public SupabaseRatingRepository(SpringDataRatingJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Rating rating) {
        jpaRepository.save(new RatingJpaEntity(
                rating.id().value(),
                rating.viewerId().value(),
                rating.contentId().value(),
                (short) rating.stars().value(),
                rating.ratedAt()
        ));
    }

    @Override
    public Optional<Rating> ofViewerAndContent(ViewerId viewerId, ContentId contentId) {
        return jpaRepository.findByViewerIdAndContentId(viewerId.value(), contentId.value()).map(this::toDomain);
    }

    @Override
    public List<Rating> ofViewer(ViewerId viewerId) {
        return jpaRepository.findAllByViewerId(viewerId.value()).stream().map(this::toDomain).toList();
    }

    private Rating toDomain(RatingJpaEntity row) {
        return new Rating(
                new RatingId(row.id()),
                new ViewerId(row.viewerId()),
                new ContentId(row.contentId()),
                new StarRating(row.stars()),
                row.ratedAt()
        );
    }
}
