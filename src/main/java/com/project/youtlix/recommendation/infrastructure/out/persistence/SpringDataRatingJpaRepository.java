package com.project.youtlix.recommendation.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data repository for recommendation.ratings rows.
 */
public interface SpringDataRatingJpaRepository extends JpaRepository<RatingJpaEntity, UUID> {

    /** Finds one rating by viewer and content. */
    Optional<RatingJpaEntity> findByViewerIdAndContentId(UUID viewerId, UUID contentId);

    /** Finds all viewer ratings. */
    List<RatingJpaEntity> findAllByViewerId(UUID viewerId);
}
