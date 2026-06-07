package com.project.youtlix.contentlibrary.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for library.contents rows.
 */
public interface SpringDataContentJpaRepository extends JpaRepository<ContentJpaEntity, UUID> {

    /** Returns available rows ordered as a simple popularity placeholder. */
    List<ContentJpaEntity> findByAvailableTrueOrderByReleaseYearDesc();
}
