package com.project.youtlix.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/youtlix/app")
public class YoutlixRestController {

    @GetMapping("/videos/recommended")
    public String getRecommendedVideos() {
        return "";
    }
}
