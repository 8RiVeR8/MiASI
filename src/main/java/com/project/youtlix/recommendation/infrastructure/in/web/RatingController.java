package com.project.youtlix.recommendation.infrastructure.in.web;

import com.project.youtlix.authentication.application.port.out.IdentityProvider;
import com.project.youtlix.common.infrastructure.in.web.OpenApiConfig;
import com.project.youtlix.contentlibrary.infrastructure.in.web.ContentResponse;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.StarRating;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Driving web adapter for PU12 ratings.
 */
@RestController
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class RatingController {

    private final RecommendationUseCase useCase;
    private final IdentityProvider identityProvider;

    /** Creates rating web adapter. */
    public RatingController(RecommendationUseCase useCase, IdentityProvider identityProvider) {
        this.useCase = useCase;
        this.identityProvider = identityProvider;
    }

    /** Rates content for a viewer. */
    @PostMapping("/rate/{contentId}")
    public void rate(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID contentId,
            @RequestBody RatingRequest request
    ) {
        useCase.rate(currentViewer(authorization), new ContentId(contentId), new StarRating(request.stars()));
    }

    @GetMapping("/recommended/library")
    public List<ContentResponse> recommendedLibrary(@RequestHeader("Authorization") String authorization) {
        return useCase.toContentResponses(useCase.generateFor(currentViewer(authorization)));
    }

    private ViewerId currentViewer(String authorization) {
        return new ViewerId(identityProvider.currentIdentity(bearerToken(authorization)).viewerId().value());
    }

    private String bearerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is required");
        }
        return authorization.startsWith("Bearer ") ? authorization.substring("Bearer ".length()) : authorization;
    }
}
