package com.project.youtlix.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PlaybackRequest {

    private UUID playableId;
    private String playableType; // MOVIE / EPISODE
    private Integer positionSeconds;
}
