package com.project.youtlix.service;

import com.project.youtlix.entity.Content;
import com.project.youtlix.repository.ContentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentService {

    private final ContentRepository repository;

    public ContentService(ContentRepository repository) {
        this.repository = repository;
    }

    public List<Content> getAll() {
        return repository.findAll();
    }
}
