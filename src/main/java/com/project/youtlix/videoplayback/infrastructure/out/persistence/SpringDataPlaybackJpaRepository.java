package com.project.youtlix.videoplayback.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data repository for playback.playbacks rows.
 */
public interface SpringDataPlaybackJpaRepository extends JpaRepository<PlaybackJpaEntity, UUID> {

    /** Finds playback by viewer and content. */
    Optional<PlaybackJpaEntity> findByViewerIdAndContentId(UUID viewerId, UUID contentId);

    /** Finds all viewer playbacks. */
    List<PlaybackJpaEntity> findAllByViewerId(UUID viewerId);
}
