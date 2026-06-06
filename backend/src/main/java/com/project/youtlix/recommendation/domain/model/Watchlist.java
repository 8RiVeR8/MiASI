package com.project.youtlix.recommendation.domain.model;

import com.project.youtlix.common.domain.DomainEvent;
import com.project.youtlix.recommendation.domain.model.event.AddedToWatchlist;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Aggregate root representing the viewer watchlist from PU13. */
public class Watchlist {
    private final WatchlistId id;
    private final ViewerId viewerId;
    private final List<WatchlistItem> items = new ArrayList<>();
    private final List<DomainEvent> events = new ArrayList<>();

    public Watchlist(WatchlistId id, ViewerId viewerId) { this.id = id; this.viewerId = viewerId; }
    /** Creates an empty watchlist for a viewer. */
    public static Watchlist emptyFor(ViewerId viewerId) { return new Watchlist(WatchlistId.newId(), viewerId); }

    /** Adds content if it is not already present. */
    public void add(ContentId contentId) {
        if (!contains(contentId)) {
            items.add(new WatchlistItem(contentId, Instant.now()));
            events.add(new AddedToWatchlist(viewerId, contentId, Instant.now()));
        }
    }

    /** Removes content from the watchlist. */
    public void remove(ContentId contentId) { items.removeIf(item -> item.contentId().equals(contentId)); }
    /** Checks whether the watchlist contains content. */
    public boolean contains(ContentId contentId) { return items.stream().anyMatch(item -> item.contentId().equals(contentId)); }
    /** Returns true when the watchlist has no items. */
    public boolean isEmpty() { return items.isEmpty(); }
    /** Returns domain events recorded by this aggregate. */
    public List<DomainEvent> occurredEvents() { return List.copyOf(events); }
    public WatchlistId id() { return id; }
    public ViewerId viewerId() { return viewerId; }
    public List<WatchlistItem> items() { return List.copyOf(items); }
}
