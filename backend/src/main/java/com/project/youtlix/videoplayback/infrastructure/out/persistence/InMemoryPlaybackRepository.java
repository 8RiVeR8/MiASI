package com.project.youtlix.videoplayback.infrastructure.out.persistence;

import com.project.youtlix.videoplayback.application.port.out.PlaybackRepository;
import com.project.youtlix.videoplayback.domain.model.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Temporary in-memory adapter for playback history until SQL persistence is implemented. */
@Repository
public class InMemoryPlaybackRepository implements PlaybackRepository {
    private final Map<PlaybackId, Playback> playbacks = new ConcurrentHashMap<>();
    @Override public void save(Playback playback) { playbacks.put(playback.id(), playback); }
    @Override public Optional<Playback> ofId(PlaybackId id) { return Optional.ofNullable(playbacks.get(id)); }
    @Override public Optional<Playback> ofViewerAndContent(ViewerId viewerId, ContentId contentId) {
        return playbacks.values().stream().filter(p -> p.viewerId().equals(viewerId) && p.contentId().equals(contentId)).findFirst();
    }
    @Override public List<Playback> ofViewer(ViewerId viewerId) { return playbacks.values().stream().filter(p -> p.viewerId().equals(viewerId)).toList(); }
}
