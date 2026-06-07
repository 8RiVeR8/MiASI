package com.project.youtlix.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PlaybackResponse {

    private UUID playableId;
    private String playableType;
    private Integer positionSeconds;
    private String status;
}
