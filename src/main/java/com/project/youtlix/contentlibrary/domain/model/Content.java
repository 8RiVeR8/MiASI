package com.project.youtlix.contentlibrary.domain.model;

import com.project.youtlix.common.domain.model.DomainEvent;
import com.project.youtlix.contentlibrary.domain.model.event.ContentAdded;
import com.project.youtlix.contentlibrary.domain.model.event.ContentModified;
import com.project.youtlix.contentlibrary.domain.model.event.ContentRemoved;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root for content available in the VOD catalog.
 */
public abstract class Content {

    private final ContentId id;
    private Metadata metadata;
    private boolean available;
    private final List<DomainEvent> occurredEvents = new ArrayList<>();

    /**
     * Creates a content aggregate and records ContentAdded.
     *
     * @param id content id
     * @param metadata descriptive metadata
     */
    protected Content(ContentId id, Metadata metadata) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.metadata = Objects.requireNonNull(metadata, "metadata must not be null");
        this.available = false;
        recordEvent(new ContentAdded(id, metadata.title(), Instant.now()));
    }

    /**
     * Updates metadata and records a modification event.
     *
     * @param metadata new metadata
     */
    public void updateMetadata(Metadata metadata) {
        this.metadata = Objects.requireNonNull(metadata, "metadata must not be null");
        recordEvent(new ContentModified(id, Instant.now()));
    }

    /**
     * Publishes content in the catalog.
     */
    public void publish() {
        this.available = true;
    }

    /**
     * Withdraws content from the catalog and records removal from public offer.
     */
    public void withdraw() {
        this.available = false;
        recordEvent(new ContentRemoved(id, Instant.now()));
    }

    /**
     * Returns events produced by this aggregate.
     *
     * @return immutable domain event list
     */
    public List<DomainEvent> occurredEvents() {
        return Collections.unmodifiableList(occurredEvents);
    }

    /**
     * Records a domain event inside the aggregate.
     *
     * @param event event to store
     */
    protected void recordEvent(DomainEvent event) {
        occurredEvents.add(event);
    }

    public ContentId id() {
        return id;
    }

    public Metadata metadata() {
        return metadata;
    }

    public boolean available() {
        return available;
    }
}
