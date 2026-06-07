package com.project.youtlix.recommendation.domain.model;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.common.domain.model.DomainEvent;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.event.AddedToWatchlist;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root representing viewer watchlist.
 */
public class Watchlist {

    private final WatchlistId id;
    private final ViewerId viewerId;
    private final List<WatchlistItem> items;
    private final List<DomainEvent> occurredEvents = new ArrayList<>();

    /** Creates an empty watchlist aggregate. */
    public Watchlist(WatchlistId id, ViewerId viewerId) {
        this(id, viewerId, List.of());
    }

    /** Creates a watchlist aggregate with existing items. */
    public Watchlist(WatchlistId id, ViewerId viewerId, List<WatchlistItem> items) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.viewerId = Objects.requireNonNull(viewerId, "viewerId must not be null");
        this.items = new ArrayList<>(items == null ? List.of() : items);
    }

    /** Adds content to watchlist if it is not already present. */
    public void add(ContentId contentId) {
        if (contains(contentId)) {
            return;
        }
        items.add(new WatchlistItem(contentId, Instant.now()));
        occurredEvents.add(new AddedToWatchlist(viewerId, contentId, Instant.now()));
    }

    /** Removes content from watchlist. */
    public void remove(ContentId contentId) {
        items.removeIf(item -> item.contentId().equals(contentId));
    }

    /** Checks whether content is already on watchlist. */
    public boolean contains(ContentId contentId) {
        return items.stream().anyMatch(item -> item.contentId().equals(contentId));
    }

    /** Checks whether watchlist is empty. */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** Returns events produced by this aggregate. */
    public List<DomainEvent> occurredEvents() {
        return Collections.unmodifiableList(occurredEvents);
    }

    public WatchlistId id() {
        return id;
    }

    public ViewerId viewerId() {
        return viewerId;
    }

    public List<WatchlistItem> items() {
        return Collections.unmodifiableList(items);
    }
}
