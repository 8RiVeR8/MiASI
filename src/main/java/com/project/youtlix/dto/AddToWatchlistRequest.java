package com.project.youtlix.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddToWatchlistRequest {
    private UUID contentId;
}
