package com.project.youtlix.recommendation.domain.model;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.common.domain.model.DomainEvent;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.event.ContentRated;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root representing one rating for a viewer-content pair.
 */
public class Rating {

    private final RatingId id;
    private final ViewerId viewerId;
    private final ContentId contentId;
    private StarRating stars;
    private Instant ratedAt;
    private final List<DomainEvent> occurredEvents = new ArrayList<>();

    /** Creates a rating aggregate. */
    public Rating(RatingId id, ViewerId viewerId, ContentId contentId, StarRating stars, Instant ratedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.viewerId = Objects.requireNonNull(viewerId, "viewerId must not be null");
        this.contentId = Objects.requireNonNull(contentId, "contentId must not be null");
        this.stars = Objects.requireNonNull(stars, "stars must not be null");
        this.ratedAt = Objects.requireNonNull(ratedAt, "ratedAt must not be null");
        occurredEvents.add(new ContentRated(viewerId, contentId, stars, ratedAt));
    }

    /** Changes rating value and emits ContentRated event. */
    public void changeTo(StarRating stars) {
        this.stars = Objects.requireNonNull(stars, "stars must not be null");
        this.ratedAt = Instant.now();
        occurredEvents.add(new ContentRated(viewerId, contentId, stars, ratedAt));
    }

    /** Returns events produced by this aggregate. */
    public List<DomainEvent> occurredEvents() {
        return Collections.unmodifiableList(occurredEvents);
    }

    public RatingId id() {
        return id;
    }

    public ViewerId viewerId() {
        return viewerId;
    }

    public ContentId contentId() {
        return contentId;
    }

    public StarRating stars() {
        return stars;
    }

    public Instant ratedAt() {
        return ratedAt;
    }
}
