package com.project.youtlix.testsupport.fixture.memory;

import com.project.youtlix.videoplayback.application.port.out.PlaybackRepository;
import com.project.youtlix.videoplayback.domain.model.ContentId;
import com.project.youtlix.videoplayback.domain.model.Playback;
import com.project.youtlix.videoplayback.domain.model.PlaybackId;
import com.project.youtlix.videoplayback.domain.model.ViewerId;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryPlaybackRepository implements PlaybackRepository {

    private final Map<PlaybackId, Playback> playbacks = new LinkedHashMap<>();

    @Override
    public void save(Playback playback) {
        playbacks.put(playback.id(), playback);
    }

    @Override
    public Optional<Playback> ofId(PlaybackId id) {
        return Optional.ofNullable(playbacks.get(id));
    }

    @Override
    public Optional<Playback> ofViewerAndContent(ViewerId viewerId, ContentId contentId) {
        return playbacks.values().stream()
                .filter(playback -> playback.viewerId().equals(viewerId) && playback.contentId().equals(contentId))
                .findFirst();
    }

    @Override
    public List<Playback> ofViewer(ViewerId viewerId) {
        return playbacks.values().stream()
                .filter(playback -> playback.viewerId().equals(viewerId))
                .toList();
    }

    public Playback onlyPlayback() {
        return playbacks.values().iterator().next();
    }
}
