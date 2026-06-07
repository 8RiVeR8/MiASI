package com.project.youtlix.service;

import com.project.youtlix.entity.library.Content;
import com.project.youtlix.repository.ContentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContentService {

    private final ContentRepository repository;

    public ContentService(ContentRepository repository) {
        this.repository = repository;
    }

    public List<Content> getAll() {
        return repository.findAll();
    }

    public Content save(Content contents) {
        return repository.save(contents);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
