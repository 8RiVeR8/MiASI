package com.project.youtlix.recommendation.infrastructure.in.web;

import com.project.youtlix.authentication.application.port.out.IdentityProvider;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Driving web adapter for PU13 watchlist management.
 */
@RestController
@RequestMapping("/watchlist")
public class WatchlistController {

    private final RecommendationUseCase useCase;
    private final IdentityProvider identityProvider;

    /** Creates watchlist web adapter. */
    public WatchlistController(RecommendationUseCase useCase, IdentityProvider identityProvider) {
        this.useCase = useCase;
        this.identityProvider = identityProvider;
    }

    /** Adds content to watchlist. */
    @PostMapping("/{contentId}")
    public void add(@RequestHeader("Authorization") String authorization, @PathVariable UUID contentId) {
        useCase.addToWatchlist(currentViewer(authorization), new ContentId(contentId));
    }

    /** Removes content from watchlist. */
    @DeleteMapping("/{contentId}")
    public void remove(@RequestHeader("Authorization") String authorization, @PathVariable UUID contentId) {
        useCase.removeFromWatchlist(currentViewer(authorization), new ContentId(contentId));
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
