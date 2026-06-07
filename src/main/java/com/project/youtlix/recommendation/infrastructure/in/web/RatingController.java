package com.project.youtlix.recommendation.infrastructure.in.web;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.domain.model.StarRating;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Driving web adapter for PU12 ratings.
 */
@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RecommendationUseCase useCase;

    /** Creates rating web adapter. */
    public RatingController(RecommendationUseCase useCase) {
        this.useCase = useCase;
    }

    /** Rates content for a viewer. */
    @PostMapping("/{viewerId}")
    public void rate(@PathVariable UUID viewerId, @RequestBody RatingRequest request) {
        useCase.rate(new ViewerId(viewerId), new ContentId(request.contentId()), new StarRating(request.stars()));
    }
}
