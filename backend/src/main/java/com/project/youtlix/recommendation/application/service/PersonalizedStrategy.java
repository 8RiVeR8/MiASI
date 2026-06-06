package com.project.youtlix.recommendation.application.service;

import com.project.youtlix.recommendation.application.port.out.ContentCatalogPort;
import com.project.youtlix.recommendation.application.port.out.WatchActivityPort;
import com.project.youtlix.recommendation.domain.model.*;
import com.project.youtlix.recommendation.domain.service.RecommendationStrategy;
import org.springframework.stereotype.Component;
import java.time.Instant;

/** Personalized implementation of PU11 based on ratings, watchlist and activity signals. */
@Component
public class PersonalizedStrategy implements RecommendationStrategy {
    private final ContentCatalogPort catalog;
    private final WatchActivityPort watchActivity;
    public PersonalizedStrategy(ContentCatalogPort catalog, WatchActivityPort watchActivity) { this.catalog = catalog; this.watchActivity = watchActivity; }
    @Override public RecommendationList recommend(ViewerId viewerId) {
        var watched = watchActivity.watchedBy(viewerId).stream().map(activity -> activity.contentId()).toList();
        var items = catalog.popularContent(10).stream().filter(id -> !watched.contains(id))
                .map(id -> new RecommendedItem(id, 1.5, RecommendationReason.PERSONALIZED)).toList();
        return new RecommendationList(viewerId, Instant.now(), items);
    }
}
