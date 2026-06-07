package com.project.youtlix.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RecommendationResponse {

    private UUID id;
    private String title;
    private String genre;
    private Integer releaseYear;
}
