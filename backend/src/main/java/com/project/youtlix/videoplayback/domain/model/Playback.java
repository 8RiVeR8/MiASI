package com.project.youtlix.videoplayback.domain.model;

import com.project.youtlix.common.domain.DomainEvent;
import com.project.youtlix.videoplayback.domain.model.event.PlaybackFinished;
import com.project.youtlix.videoplayback.domain.model.event.PlaybackProgressSaved;
import com.project.youtlix.videoplayback.domain.model.event.PlaybackStarted;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Aggregate root representing start, resume and progress of a video playback. */
public class Playback {
    private final PlaybackId id;
    private final ViewerId viewerId;
    private final ContentId contentId;
    private PlaybackProgress progress;
    private PlaybackStatus status;
    private final List<DomainEvent> events = new ArrayList<>();

    public Playback(PlaybackId id, ViewerId viewerId, ContentId contentId, PlaybackProgress progress, PlaybackStatus status) {
        this.id = id; this.viewerId = viewerId; this.contentId = contentId; this.progress = progress; this.status = status;
    }

    /** Creates new playback history for viewer and content. */
    public static Playback create(ViewerId viewerId, ContentId contentId) {
        return new Playback(PlaybackId.newId(), viewerId, contentId, PlaybackProgress.start(), PlaybackStatus.PAUSED);
    }

    /** Starts or resumes playback from the provided progress. */
    public void start(PlaybackProgress from) {
        this.progress = from;
        this.status = PlaybackStatus.PLAYING;
        events.add(new PlaybackStarted(viewerId, contentId, from, Instant.now()));
    }

    /** Saves current playback progress. */
    public void updateProgress(PlaybackProgress progress) {
        this.progress = progress;
        this.status = PlaybackStatus.PAUSED;
        events.add(new PlaybackProgressSaved(viewerId, contentId, progress, Instant.now()));
    }

    /** Marks playback as completed. */
    public void complete() {
        this.status = PlaybackStatus.COMPLETED;
        events.add(new PlaybackFinished(viewerId, contentId, Instant.now()));
    }

    /** Returns true when playback can be resumed from saved progress. */
    public boolean isResumable() { return status != PlaybackStatus.COMPLETED && !progress.isStart(); }
    /** Returns domain events recorded by this aggregate. */
    public List<DomainEvent> occurredEvents() { return List.copyOf(events); }
    public PlaybackId id() { return id; }
    public ViewerId viewerId() { return viewerId; }
    public ContentId contentId() { return contentId; }
    public PlaybackProgress progress() { return progress; }
    public PlaybackStatus status() { return status; }
}
