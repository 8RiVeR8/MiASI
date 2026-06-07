package com.project.youtlix.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RatingRequest {
    private UUID contentId;
    private Short stars;
}
