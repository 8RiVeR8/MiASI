package com.project.youtlix.service;

import com.project.youtlix.dto.RecommendationResponse;
import com.project.youtlix.entity.enums.Genre;
import com.project.youtlix.entity.enums.PlaybackStatus;
import com.project.youtlix.entity.library.Content;
import com.project.youtlix.entity.playback.Playback;
import com.project.youtlix.entity.recommendation.Rating;
import com.project.youtlix.repository.ContentRepository;
import com.project.youtlix.repository.playback.PlaybackRepository;
import com.project.youtlix.repository.recommendation.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final RatingRepository ratingRepository;
    private final PlaybackRepository playbackRepository;
    private final ContentRepository contentRepository;

    public RecommendationService(
            RatingRepository ratingRepository,
            PlaybackRepository playbackRepository,
            ContentRepository contentRepository
    ) {
        this.ratingRepository = ratingRepository;
        this.playbackRepository = playbackRepository;
        this.contentRepository = contentRepository;
    }

    public List<RecommendationResponse> recommend(UUID viewerId) {

        Map<Genre, Integer> genreScore = new HashMap<>();

        // Ratings
        List<Rating> ratings = ratingRepository.findAllByViewerId(viewerId);

        for (Rating rating : ratings) {

            if (rating.getStars() < 4) {
                continue;
            }

            contentRepository.findById(rating.getContentId())
                    .ifPresent(content -> {
                        genreScore.merge(
                                content.getGenre(),
                                rating.getStars().intValue(),
                                Integer::sum
                        );
                    });
        }

        // Playback history
        List<Playback> playbacks =
                playbackRepository.findAllByViewerId(viewerId);

        for (Playback playback : playbacks) {

            if (playback.getStatus() != PlaybackStatus.COMPLETED) {
                continue;
            }

            contentRepository.findById(playback.getPlayableId())
                    .ifPresent(content -> {
                        genreScore.merge(
                                content.getGenre(),
                                3,
                                Integer::sum
                        );
                    });
        }

        if (genreScore.isEmpty()) {
            return List.of();
        }

        Genre favoriteGenre =
                Collections.max(
                        genreScore.entrySet(),
                        Map.Entry.comparingByValue()
                ).getKey();

        Set<UUID> excluded = ratings.stream()
                .map(Rating::getContentId)
                .collect(Collectors.toSet());

        List<Content> recommendations =
                contentRepository.findByGenreAndAvailableTrue(favoriteGenre);

        return recommendations.stream()
                .filter(c -> !excluded.contains(c.getId()))
                .limit(20)
                .map(c -> new RecommendationResponse(
                        c.getId(),
                        c.getTitle(),
                        c.getGenre().name(),
                        c.getReleaseYear()
                ))
                .toList();
    }
}
