package com.project.youtlix.videoplayback.domain.model;

import com.project.youtlix.videoplayback.domain.model.event.PlaybackFinished;
import com.project.youtlix.videoplayback.domain.model.event.PlaybackProgressSaved;
import com.project.youtlix.videoplayback.domain.model.event.PlaybackStarted;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root representing one viewer playback for one content item.
 */
public class Playback {

    private final PlaybackId id;
    private final ViewerId viewerId;
    private final ContentId contentId;
    private final PlayableType playableType;
    private PlaybackProgress progress;
    private PlaybackStatus status;
    private final List<DomainEvent> occurredEvents = new ArrayList<>();

    /**
     * Creates a playback aggregate.
     */
    public Playback(PlaybackId id, ViewerId viewerId, ContentId contentId, PlayableType playableType) {
        this(id, viewerId, contentId, playableType, PlaybackProgress.start(), PlaybackStatus.PAUSED);
    }

    /**
     * Recreates a playback aggregate from persistence.
     */
    public Playback(
            PlaybackId id,
            ViewerId viewerId,
            ContentId contentId,
            PlayableType playableType,
            PlaybackProgress progress,
            PlaybackStatus status
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.viewerId = Objects.requireNonNull(viewerId, "viewerId must not be null");
        this.contentId = Objects.requireNonNull(contentId, "contentId must not be null");
        this.playableType = Objects.requireNonNull(playableType, "playableType must not be null");
        this.progress = Objects.requireNonNull(progress, "progress must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    /**
     * Starts or resumes playback from given progress.
     *
     * @param from progress to resume from
     */
    public void start(PlaybackProgress from) {
        this.progress = Objects.requireNonNull(from, "from must not be null");
        this.status = PlaybackStatus.PLAYING;
        occurredEvents.add(new PlaybackStarted(viewerId, contentId, from, Instant.now()));
    }

    /**
     * Saves playback progress and pauses the aggregate.
     *
     * @param progress current playback progress
     */
    public void updateProgress(PlaybackProgress progress) {
        this.progress = Objects.requireNonNull(progress, "progress must not be null");
        this.status = PlaybackStatus.PAUSED;
        occurredEvents.add(new PlaybackProgressSaved(viewerId, contentId, progress, Instant.now()));
    }

    /**
     * Marks playback as completed.
     */
    public void complete() {
        this.status = PlaybackStatus.COMPLETED;
        occurredEvents.add(new PlaybackFinished(viewerId, contentId, Instant.now()));
    }

    /**
     * Checks whether playback can be resumed.
     *
     * @return true when there is progress and playback is not completed
     */
    public boolean isResumable() {
        return status != PlaybackStatus.COMPLETED && !progress.isStart();
    }

    /** Returns events produced by this aggregate. */
    public List<DomainEvent> occurredEvents() {
        return Collections.unmodifiableList(occurredEvents);
    }

    public PlaybackId id() {
        return id;
    }

    public ViewerId viewerId() {
        return viewerId;
    }

    public ContentId contentId() {
        return contentId;
    }

    public PlayableType playableType() {
        return playableType;
    }

    public PlaybackProgress progress() {
        return progress;
    }

    public PlaybackStatus status() {
        return status;
    }
}
