package com.project.youtlix.controller.library;

import com.project.youtlix.entity.library.Content;
import com.project.youtlix.service.ContentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/contents")
public class ContentController {

    private final ContentService service;

    public ContentController(ContentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Content> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Content create(@RequestBody Content content) {
        return service.save(content);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.deleteById(id);
    }
}
