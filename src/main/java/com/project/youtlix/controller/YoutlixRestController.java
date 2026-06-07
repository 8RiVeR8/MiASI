package com.project.youtlix.controller;

import com.project.youtlix.entity.Content;
import com.project.youtlix.service.ContentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/youtlix/app")
public class YoutlixRestController {

    private final ContentService service;

    public YoutlixRestController(ContentService service) {
        this.service = service;
    }

    @GetMapping("/videos/recommended")
    public String getRecommendedVideos() {
        return "";
    }

    @GetMapping("/content")
    public List<Content> getAll() {
        return service.getAll();
    }
}
