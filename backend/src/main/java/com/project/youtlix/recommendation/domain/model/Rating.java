package com.project.youtlix.recommendation.domain.model;

import com.project.youtlix.common.domain.DomainEvent;
import com.project.youtlix.recommendation.domain.model.event.ContentRated;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Aggregate root representing one rating for a pair of viewer and content. */
public class Rating {
    private final RatingId id;
    private final ViewerId viewerId;
    private final ContentId contentId;
    private StarRating stars;
    private Instant ratedAt;
    private final List<DomainEvent> events = new ArrayList<>();

    public Rating(RatingId id, ViewerId viewerId, ContentId contentId, StarRating stars, Instant ratedAt) {
        this.id = id;
        this.viewerId = viewerId;
        this.contentId = contentId;
        this.stars = stars;
        this.ratedAt = ratedAt;
        events.add(new ContentRated(viewerId, contentId, stars, ratedAt));
    }

    /** Creates a new rating for viewer and content. */
    public static Rating create(ViewerId viewerId, ContentId contentId, StarRating stars) {
        return new Rating(RatingId.newId(), viewerId, contentId, stars, Instant.now());
    }

    /** Changes the rating value and records an event. */
    public void changeTo(StarRating stars) {
        this.stars = stars;
        this.ratedAt = Instant.now();
        events.add(new ContentRated(viewerId, contentId, stars, ratedAt));
    }

    /** Returns domain events recorded by this aggregate. */
    public List<DomainEvent> occurredEvents() { return List.copyOf(events); }
    public RatingId id() { return id; }
    public ViewerId viewerId() { return viewerId; }
    public ContentId contentId() { return contentId; }
    public StarRating stars() { return stars; }
    public Instant ratedAt() { return ratedAt; }
}
