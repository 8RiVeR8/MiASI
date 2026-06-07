package com.project.youtlix.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class WatchlistItemResponse {
    private UUID id;
    private UUID contentId;
}
