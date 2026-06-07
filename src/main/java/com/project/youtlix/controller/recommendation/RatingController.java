package com.project.youtlix.controller.recommendation;

import com.project.youtlix.dto.RatingRequest;
import com.project.youtlix.dto.RatingResponse;
import com.project.youtlix.entity.recommendation.Rating;
import com.project.youtlix.service.RatingService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService service;

    public RatingController(RatingService service) {
        this.service = service;
    }

    @PostMapping("/{viewerId}")
    public RatingResponse rate(@PathVariable UUID viewerId, @RequestBody RatingRequest request) {
        Rating rating = service.rate(viewerId, request.getContentId(), request.getStars());
        return new RatingResponse(rating.getContentId(), rating.getStars());
    }

    @GetMapping("/{viewerId}/{contentId}")
    public RatingResponse get(@PathVariable UUID viewerId, @PathVariable UUID contentId) {
        Rating rating = service.getRating(viewerId, contentId);
        if (rating == null) {
            return null;
        }
        return new RatingResponse(rating.getContentId(), rating.getStars());
    }

    @DeleteMapping("/{viewerId}/{contentId}")
    public void delete(@PathVariable UUID viewerId, @PathVariable UUID contentId) {
        service.deleteRating(viewerId, contentId);
    }
}