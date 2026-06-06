package com.project.youtlix.contentlibrary.domain.model;

import com.project.youtlix.common.domain.DomainEvent;
import com.project.youtlix.contentlibrary.domain.model.event.ContentAdded;
import com.project.youtlix.contentlibrary.domain.model.event.ContentModified;
import com.project.youtlix.contentlibrary.domain.model.event.ContentRemoved;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Aggregate root representing a library item: movie or series. */
public abstract class Content {
    private final ContentId id;
    private Metadata metadata;
    private boolean available;
    private final List<DomainEvent> events = new ArrayList<>();

    protected Content(ContentId id, Metadata metadata) {
        this.id = id;
        this.metadata = metadata;
        this.available = false;
        events.add(new ContentAdded(id, metadata.title(), Instant.now()));
    }

    /** Updates content metadata and records a modification event. */
    public void updateMetadata(Metadata metadata) {
        this.metadata = metadata;
        events.add(new ContentModified(id, Instant.now()));
    }

    /** Makes content visible in the library. */
    public void publish() { available = true; }

    /** Withdraws content from the library and records a removal event. */
    public void withdraw() {
        available = false;
        events.add(new ContentRemoved(id, Instant.now()));
    }

    /** Returns domain events recorded by this aggregate. */
    public List<DomainEvent> occurredEvents() { return List.copyOf(events); }
    public ContentId id() { return id; }
    public Metadata metadata() { return metadata; }
    public boolean available() { return available; }
}
