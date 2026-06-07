package com.project.youtlix.service;

import com.project.youtlix.entity.enums.PlaybackStatus;
import com.project.youtlix.entity.playback.Playback;
import com.project.youtlix.repository.playback.PlaybackRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PlaybackService {

    private final PlaybackRepository repository;

    public PlaybackService(PlaybackRepository repository) {
        this.repository = repository;
    }

    public Playback saveProgress(
            UUID viewerId,
            UUID playableId,
            String playableType,
            int positionSeconds,
            int totalDurationSeconds
    ) {

        Playback playback = repository
                .findByViewerIdAndPlayableTypeAndPlayableId(viewerId, playableType, playableId)
                .orElse(null);

        if (playback == null) {
            playback = new Playback();
            playback.setId(UUID.randomUUID());
            playback.setViewerId(viewerId);
            playback.setPlayableId(playableId);
            playback.setPlayableType(playableType);
            playback.setCreatedAt(LocalDateTime.now());
        }

        playback.setPositionSeconds(positionSeconds);
        playback.setProgressUpdatedAt(LocalDateTime.now());
        playback.setUpdatedAt(LocalDateTime.now());

        // auto-complete at 90%
        double progress = (double) positionSeconds / (double) totalDurationSeconds;

        if (progress >= 0.90) {
            playback.setStatus(PlaybackStatus.COMPLETED);
        } else if (positionSeconds > 0) {
            playback.setStatus(PlaybackStatus.PLAYING);
        } else {
            playback.setStatus(PlaybackStatus.PAUSED);
        }

        return repository.save(playback);
    }

    public List<Playback> getContinueWatching(UUID viewerId) {
        return repository.findAllByViewerIdAndStatusNot(viewerId, PlaybackStatus.COMPLETED);
    }
}
