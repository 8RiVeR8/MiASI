package com.project.youtlix.contentlibrary.infrastructure.in.web;

import com.project.youtlix.authentication.application.port.out.IdentityProvider;
import com.project.youtlix.authentication.domain.model.UserIdentity;
import com.project.youtlix.contentlibrary.application.port.in.ContentNotFoundException;
import com.project.youtlix.contentlibrary.application.port.in.ContentLibraryUseCase;
import com.project.youtlix.contentlibrary.application.port.in.SeasonNotFoundException;
import com.project.youtlix.contentlibrary.application.port.in.SeriesContentExpectedException;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Duration;
import com.project.youtlix.contentlibrary.domain.model.EpisodeId;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Page;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;
import com.project.youtlix.contentlibrary.domain.model.SeasonId;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.domain.model.RecommendedItem;
import com.project.youtlix.common.infrastructure.in.web.OpenApiConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
import java.util.Map;
import java.util.UUID;

/**
 * Driving web adapter for content library use cases.
 */
@RestController
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
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
        Page requestedPage = toPage(page, size);
        List<ContentResponse> contents = useCase.browse(requestedPage).stream()
                .map(ContentResponse::from)
                .toList();
        List<RecommendedContentResponse> recommendations = recommendationUseCase.generateFor(
                        new com.project.youtlix.recommendation.domain.model.ViewerId(identity.viewerId().value())
                )
                .items()
                .stream()
                .map(RecommendedContentResponse::from)
                .toList();
        return new LibraryPageResponse(
                contents,
                recommendations,
                new LibraryPaginationResponse(page, size, contents.size())
        );
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
        return useCase.searchByKeyword(requiredPhrase(phrase)).stream().map(ContentResponse::from).toList();
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
        SearchCriteria criteria = toSearchCriteria(phrase, genre, yearFrom, yearTo);
        return useCase.filter(criteria).stream().map(ContentResponse::from).toList();
    }

    /**
     * Handles PU8 adding content.
     */
    @PostMapping("/admin/content")
    @ResponseStatus(HttpStatus.CREATED)
    public UUID create(@RequestHeader("Authorization") String authorization, @RequestBody ContentRequest request) {
        requireLibraryAdmin(authorization);
        if (request.type() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type is required");
        }
        if (request.type() == ContentType.MOVIE) {
            requireCompleteMovieFields(request);
            return useCase.createMovie(
                    toMetadata(request),
                    Duration.ofSeconds(request.durationSeconds()),
                    new VideoFile(request.videoUri(), request.languages())
            ).value();
        }
        rejectMovieFieldsForSeries(request);
        return useCase.createSeries(toMetadata(request)).value();
    }

    /**
     * Adds a season to an existing series.
     */
    @PostMapping("/admin/content/{seriesId}/seasons")
    @ResponseStatus(HttpStatus.CREATED)
    public UUID addSeason(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID seriesId,
            @RequestBody SeasonRequest request
    ) {
        requireLibraryAdmin(authorization);
        return useCase.addSeason(new ContentId(seriesId), request.number(), request.title()).value();
    }

    /**
     * Adds an episode to an existing series season.
     */
    @PostMapping("/admin/content/{seriesId}/seasons/{seasonId}/episodes")
    @ResponseStatus(HttpStatus.CREATED)
    public UUID addEpisode(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID seriesId,
            @PathVariable UUID seasonId,
            @RequestBody EpisodeRequest request
    ) {
        requireLibraryAdmin(authorization);
        requireCompleteEpisodeFields(request);
        EpisodeId episodeId = useCase.addEpisode(
                new ContentId(seriesId),
                new SeasonId(seasonId),
                request.number(),
                request.title(),
                Duration.ofSeconds(request.durationSeconds()),
                new VideoFile(request.videoUri(), request.languages())
        );
        return episodeId.value();
    }

    /**
     * Handles PU9 metadata modification.
     */
    @PutMapping("/admin/content/{id}")
    public void update(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID id,
            @RequestBody ContentMetadataRequest request
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
        return toMetadata(
                request.title(),
                request.description(),
                request.thumbnailUrl(),
                request.genre(),
                request.releaseYear(),
                request.keywords()
        );
    }

    private Metadata toMetadata(ContentMetadataRequest request) {
        return toMetadata(
                request.title(),
                request.description(),
                request.thumbnailUrl(),
                request.genre(),
                request.releaseYear(),
                request.keywords()
        );
    }

    private Metadata toMetadata(
            String title,
            String description,
            String thumbnailUrl,
            Genre genre,
            int releaseYear,
            List<String> rawKeywords
    ) {
        try {
            List<Keyword> keywords = rawKeywords == null
                    ? List.of()
                    : rawKeywords.stream().map(Keyword::new).toList();
            return new Metadata(
                    title,
                    description,
                    thumbnailUrl,
                    genre,
                    releaseYear,
                    keywords
            );
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    @ExceptionHandler(ContentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleContentNotFound(ContentNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(SeasonNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleSeasonNotFound(SeasonNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(SeriesContentExpectedException.class)
    public ResponseEntity<Map<String, String>> handleSeriesExpected(SeriesContentExpectedException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", exception.getMessage()));
    }

    private UserIdentity currentIdentity(String authorization) {
        return identityProvider.currentIdentity(bearerToken(authorization));
    }

    private void requireLibraryAdmin(String authorization) {
        if (!currentIdentity(authorization).canManageLibrary()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "LIBRARY_ADMIN role required");
        }
    }

    private boolean hasMovieFields(ContentRequest request) {
        return request.durationSeconds() != null
                || request.videoUri() != null
                || request.languages() != null && !request.languages().isEmpty();
    }

    private void rejectMovieFieldsForSeries(ContentRequest request) {
        if (hasMovieFields(request)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "movie fields are not allowed for series content; add seasons and episodes separately"
            );
        }
    }

    private void requireCompleteMovieFields(ContentRequest request) {
        if (request.durationSeconds() == null || request.videoUri() == null || request.videoUri().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "durationSeconds and videoUri are required for movie content"
            );
        }
        if (request.durationSeconds() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "duration must be positive");
        }
    }

    private void requireCompleteEpisodeFields(EpisodeRequest request) {
        if (request.durationSeconds() == null || request.videoUri() == null || request.videoUri().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "durationSeconds and videoUri are required for episode"
            );
        }
        if (request.durationSeconds() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "duration must be positive");
        }
    }

    private String bearerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is required");
        }
        return authorization.startsWith("Bearer ") ? authorization.substring("Bearer ".length()) : authorization;
    }

    private String requiredPhrase(String phrase) {
        if (phrase == null || phrase.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "phrase must not be blank");
        }
        return phrase.trim();
    }

    private SearchCriteria toSearchCriteria(String phrase, Genre genre, Integer yearFrom, Integer yearTo) {
        try {
            return new SearchCriteria(optionalPhrase(phrase), genre, yearFrom, yearTo);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    private String optionalPhrase(String phrase) {
        return phrase == null || phrase.isBlank() ? null : phrase.trim();
    }

    private Page toPage(int page, int size) {
        if (page < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must be greater than or equal to 1");
        }
        if (size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size must be greater than or equal to 1");
        }
        return new Page(page - 1, size);
    }

    public record LibraryPageResponse(
            List<ContentResponse> contents,
            List<RecommendedContentResponse> recommendations,
            LibraryPaginationResponse pagination
    ) {
    }

    public record LibraryPaginationResponse(
            int page,
            int size,
            int itemCount
    ) {
    }

    public record RecommendedContentResponse(UUID contentId, double score, String reason) {
        static RecommendedContentResponse from(RecommendedItem item) {
            return new RecommendedContentResponse(item.contentId().value(), item.score(), item.reason().name());
        }
    }
}
