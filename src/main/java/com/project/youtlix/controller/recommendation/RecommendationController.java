package com.project.youtlix.controller.recommendation;

import com.project.youtlix.dto.RecommendationResponse;
import com.project.youtlix.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @GetMapping("/{viewerId}")
    public List<RecommendationResponse> getRecommendations(@PathVariable UUID viewerId) {
        return service.recommend(viewerId);
    }
}
