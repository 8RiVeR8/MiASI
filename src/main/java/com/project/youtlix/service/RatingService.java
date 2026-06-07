package com.project.youtlix.service;


import com.project.youtlix.entity.recommendation.Rating;
import com.project.youtlix.repository.recommendation.RatingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RatingService {

    private final RatingRepository repository;

    public RatingService(RatingRepository repository) {
        this.repository = repository;
    }

    public Rating rate(UUID viewerId, UUID contentId, Short stars) {

        Rating rating = repository
                .findByViewerIdAndContentId(viewerId, contentId)
                .orElse(null);

        if (rating == null) {
            rating = new Rating();
            rating.setId(UUID.randomUUID());
            rating.setViewerId(viewerId);
            rating.setContentId(contentId);
            rating.setRatedAt(LocalDateTime.now());
        }

        rating.setStars(stars);
        rating.setRatedAt(LocalDateTime.now());

        return repository.save(rating);
    }

    public void deleteRating(UUID viewerId, UUID contentId) {
        repository.deleteByViewerIdAndContentId(viewerId, contentId);
    }

    public Rating getRating(UUID viewerId, UUID contentId) {
        return repository.findByViewerIdAndContentId(viewerId, contentId)
                .orElse(null);
    }
}
