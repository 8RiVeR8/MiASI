package com.project.youtlix.videoplayback.infrastructure.out.persistence;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.videoplayback.application.port.out.PlaybackRepository;
import com.project.youtlix.videoplayback.domain.model.Playback;
import com.project.youtlix.videoplayback.domain.model.PlaybackId;
import com.project.youtlix.videoplayback.domain.model.PlaybackProgress;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Driven adapter for Supabase Postgres playback schema.
 */
@Repository
public class SupabasePlaybackRepository implements PlaybackRepository {

    private final SpringDataPlaybackJpaRepository jpaRepository;

    /** Creates the playback persistence adapter. */
    public SupabasePlaybackRepository(SpringDataPlaybackJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Playback playback) {
        jpaRepository.save(new PlaybackJpaEntity(
                playback.id().value(),
                playback.viewerId().value(),
                playback.contentId().value(),
                "MOVIE",
                playback.progress().positionSeconds(),
                playback.progress().updatedAt(),
                playback.status()
        ));
    }

    @Override
    public Optional<Playback> ofId(PlaybackId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Playback> ofViewerAndContent(ViewerId viewerId, ContentId contentId) {
        return jpaRepository.findByViewerIdAndContentId(viewerId.value(), contentId.value()).map(this::toDomain);
    }

    @Override
    public List<Playback> ofViewer(ViewerId viewerId) {
        return jpaRepository.findAllByViewerId(viewerId.value()).stream().map(this::toDomain).toList();
    }

    private Playback toDomain(PlaybackJpaEntity row) {
        Playback playback = new Playback(
                new PlaybackId(row.id()),
                new ViewerId(row.viewerId()),
                new ContentId(row.contentId())
        );
        playback.updateProgress(new PlaybackProgress(row.positionSeconds(), row.progressUpdatedAt()));
        if (row.status() == com.project.youtlix.videoplayback.domain.model.PlaybackStatus.COMPLETED) {
            playback.complete();
        }
        return playback;
    }
}
