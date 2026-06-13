package com.project.youtlix.contentlibrary.infrastructure.in.web;

import com.project.youtlix.authentication.application.port.out.IdentityProvider;
import com.project.youtlix.authentication.domain.model.UserIdentity;
import com.project.youtlix.contentlibrary.application.port.in.ContentLibraryUseCase;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Duration;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Page;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.domain.model.RecommendedItem;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Driving web adapter for content library use cases.
 */
@RestController
public class ContentController {

    private final ContentLibraryUseCase useCase;
    private final RecommendationUseCase recommendationUseCase;
    private final IdentityProvider identityProvider;

    /**
     * Creates a web adapter around the content library inbound port.
     */
    public ContentController(
            ContentLibraryUseCase useCase,
            RecommendationUseCase recommendationUseCase,
            IdentityProvider identityProvider
    ) {
        this.useCase = useCase;
        this.recommendationUseCase = recommendationUseCase;
        this.identityProvider = identityProvider;
    }

    /**
     * Handles PU5 browsing.
     */
    @GetMapping("/library")
    public LibraryPageResponse browse(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        UserIdentity identity = currentIdentity(authorization);
        List<ContentResponse> contents = useCase.browse(new Page(toZeroBasedPage(page), size)).stream()
                .map(ContentResponse::from)
                .toList();
        List<RecommendedContentResponse> recommendations = recommendationUseCase.generateFor(
                        new com.project.youtlix.recommendation.domain.model.ViewerId(identity.viewerId().value())
                )
                .items()
                .stream()
                .map(RecommendedContentResponse::from)
                .toList();
        return new LibraryPageResponse(contents, recommendations);
    }

    /**
     * Handles PU6 keyword search.
     */
    @GetMapping("/library/search")
    public List<ContentResponse> search(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String phrase
    ) {
        currentIdentity(authorization);
        return useCase.searchByKeyword(phrase).stream().map(ContentResponse::from).toList();
    }

    /**
     * Handles PU7 filtering.
     */
    @GetMapping("/library/filter")
    public List<ContentResponse> filter(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false) String phrase,
            @RequestParam(required = false) Genre genre,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo
    ) {
        currentIdentity(authorization);
        SearchCriteria criteria = new SearchCriteria(phrase, genre, yearFrom, yearTo);
        return useCase.filter(criteria).stream().map(ContentResponse::from).toList();
    }

    /**
     * Handles PU8 adding content.
     */
    @PostMapping("/admin/content")
    @ResponseStatus(HttpStatus.CREATED)
    public UUID create(@RequestHeader("Authorization") String authorization, @RequestBody ContentRequest request) {
        requireLibraryAdmin(authorization);
        if (request.durationSeconds() != null && request.videoUri() != null) {
            return useCase.createMovie(
                    toMetadata(request),
                    Duration.ofSeconds(request.durationSeconds()),
                    new VideoFile(request.videoUri(), request.languages())
            ).value();
        }
        return useCase.createSeries(toMetadata(request)).value();
    }

    /**
     * Handles PU9 metadata modification.
     */
    @PutMapping("/admin/content/{id}")
    public void update(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID id,
            @RequestBody ContentRequest request
    ) {
        requireLibraryAdmin(authorization);
        useCase.updateMetadata(new ContentId(id), toMetadata(request));
    }

    /**
     * Handles PU10 removal.
     */
    @DeleteMapping("/admin/content/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader("Authorization") String authorization, @PathVariable UUID id) {
        requireLibraryAdmin(authorization);
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

    private UserIdentity currentIdentity(String authorization) {
        return identityProvider.currentIdentity(bearerToken(authorization));
    }

    private void requireLibraryAdmin(String authorization) {
        if (!currentIdentity(authorization).canManageLibrary()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "LIBRARY_ADMIN role required");
        }
    }

    private String bearerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is required");
        }
        return authorization.startsWith("Bearer ") ? authorization.substring("Bearer ".length()) : authorization;
    }

    private int toZeroBasedPage(int page) {
        return page <= 0 ? 0 : page - 1;
    }

    public record LibraryPageResponse(
            List<ContentResponse> contents,
            List<RecommendedContentResponse> recommendations
    ) {
    }

    public record RecommendedContentResponse(UUID contentId, double score, String reason) {
        static RecommendedContentResponse from(RecommendedItem item) {
            return new RecommendedContentResponse(item.contentId().value(), item.score(), item.reason().name());
        }
    }
}
