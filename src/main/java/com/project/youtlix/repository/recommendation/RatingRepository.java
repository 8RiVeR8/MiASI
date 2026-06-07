package com.project.youtlix.repository.recommendation;

import com.project.youtlix.entity.recommendation.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {

    Optional<Rating> findByViewerIdAndContentId(UUID viewerId, UUID contentId);

    void deleteByViewerIdAndContentId(UUID viewerId, UUID contentId);
}
