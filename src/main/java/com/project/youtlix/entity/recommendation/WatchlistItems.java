package com.project.youtlix.entity.recommendation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "recommendation", name = "watchlist_items")
public class WatchlistItems {

    @Id
    private UUID id;

    @Column(name = "watchlist_id", nullable = false)
    private UUID watchlistId;

    @Column(name = "content_id", nullable = false)
    private UUID contentId;

    @Column(name = "added_on")
    private LocalDateTime addedOn;
}
