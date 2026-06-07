package com.project.youtlix.contentlibrary.infrastructure.in.web;

import com.project.youtlix.contentlibrary.application.port.in.ContentLibraryUseCase;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Duration;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Page;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Driving web adapter for content library use cases.
 */
@RestController
@RequestMapping("/contents")
public class ContentController {

    private final ContentLibraryUseCase useCase;

    /**
     * Creates a web adapter around the content library inbound port.
     */
    public ContentController(ContentLibraryUseCase useCase) {
        this.useCase = useCase;
    }

    /**
     * Handles PU5 browsing.
     */
    @GetMapping
    public List<ContentResponse> browse(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return useCase.browse(new Page(page, size)).stream().map(ContentResponse::from).toList();
    }

    /**
     * Handles PU6 keyword search.
     */
    @GetMapping("/search")
    public List<ContentResponse> search(@RequestParam String phrase) {
        return useCase.searchByKeyword(phrase).stream().map(ContentResponse::from).toList();
    }

    /**
     * Handles PU7 filtering.
     */
    @PostMapping("/filter")
    public List<ContentResponse> filter(@RequestBody SearchCriteria criteria) {
        return useCase.filter(criteria).stream().map(ContentResponse::from).toList();
    }

    /**
     * Handles PU8 adding a movie.
     */
    @PostMapping("/movies")
    public UUID createMovie(@RequestBody ContentRequest request) {
        return useCase.createMovie(
                toMetadata(request),
                Duration.ofSeconds(request.durationSeconds()),
                new VideoFile(request.videoUri(), request.languages())
        ).value();
    }

    /**
     * Handles PU8 adding a series.
     */
    @PostMapping("/series")
    public UUID createSeries(@RequestBody ContentRequest request) {
        return useCase.createSeries(toMetadata(request)).value();
    }

    /**
     * Handles PU9 metadata modification.
     */
    @PutMapping("/{id}")
    public void update(@PathVariable UUID id, @RequestBody ContentRequest request) {
        useCase.updateMetadata(new ContentId(id), toMetadata(request));
    }

    /**
     * Handles PU10 removal.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        useCase.remove(new ContentId(id));
    }

    private Metadata toMetadata(ContentRequest request) {
        List<Keyword> keywords = request.keywords() == null
                ? List.of()
                : request.keywords().stream().map(Keyword::new).toList();
        return new Metadata(
                request.title(),
                request.description(),
                request.thumbnailUrl(),
                request.genre(),
                request.releaseYear(),
                keywords
        );
    }
}
