package com.project.youtlix.recommendation.infrastructure.in.web;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Driving web adapter for PU11 recommendation generation.
 */
@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationUseCase useCase;

    /** Creates recommendation web adapter. */
    public RecommendationController(RecommendationUseCase useCase) {
        this.useCase = useCase;
    }

    /** Generates recommendations for selected viewer. */
    @GetMapping("/{viewerId}")
    public List<RecommendationResponse> generate(@PathVariable UUID viewerId) {
        return useCase.generateFor(new ViewerId(viewerId)).items().stream()
                .map(RecommendationResponse::from)
                .toList();
    }
}
