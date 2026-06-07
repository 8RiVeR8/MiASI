package com.project.youtlix.repository.playback;

import com.project.youtlix.entity.enums.PlaybackStatus;
import com.project.youtlix.entity.playback.Playback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaybackRepository extends JpaRepository<Playback, UUID> {

    Optional<Playback> findByViewerIdAndPlayableTypeAndPlayableId(UUID viewerId, String playableType, UUID playableId);

    List<Playback> findAllByViewerIdAndStatusNot(UUID viewerId, PlaybackStatus status);

    List<Playback> findAllByViewerId(UUID viewerId);
}
