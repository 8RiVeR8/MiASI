package com.project.youtlix.contentlibrary.infrastructure.in.web;

import com.project.youtlix.contentlibrary.application.port.in.ContentLibraryUseCase;
import com.project.youtlix.contentlibrary.application.port.in.CreateMovieCommand;
import com.project.youtlix.contentlibrary.application.port.in.UpdateMetadataCommand;
import com.project.youtlix.contentlibrary.domain.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/** Driving web adapter exposing content library endpoints from PU5-PU10. */
@RestController
@RequestMapping("/library")
public class ContentLibraryController {
    private final ContentLibraryUseCase library;
    public ContentLibraryController(ContentLibraryUseCase library) { this.library = library; }

    @GetMapping
    public List<Content> browse(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return library.browse(new Page(page, size));
    }

    @GetMapping("/search")
    public List<Content> search(@RequestParam String phrase) { return library.searchByKeyword(phrase); }

    @GetMapping("/filter")
    public List<Content> filter(@RequestParam(required = false) Genre genre,
            @RequestParam(required = false) Integer yearFrom, @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) String phrase) {
        return library.filter(new SearchCriteria(phrase, genre, yearFrom, yearTo));
    }

    @PostMapping("/admin/content")
    public ResponseEntity<ContentId> createMovie(@RequestBody CreateMovieRequest request) {
        ContentId id = library.createMovie(new CreateMovieCommand(request.metadata(), request.duration(), request.videoFile()));
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PutMapping("/admin/content/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody Metadata metadata) {
        library.updateMetadata(new UpdateMetadataCommand(new ContentId(id), metadata));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/content/{id}")
    public ResponseEntity<Void> remove(@PathVariable UUID id) {
        library.removeContent(new ContentId(id));
        return ResponseEntity.noContent().build();
    }

    public record CreateMovieRequest(Metadata metadata, Duration duration, VideoFile videoFile) {}
}
