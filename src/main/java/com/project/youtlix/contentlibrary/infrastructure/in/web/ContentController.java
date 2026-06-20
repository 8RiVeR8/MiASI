package com.project.youtlix.contentlibrary.infrastructure.in.web;

import com.project.youtlix.authentication.application.port.out.IdentityProvider;
import com.project.youtlix.authentication.domain.model.UserIdentity;
import com.project.youtlix.contentlibrary.application.port.in.ContentNotFoundException;
import com.project.youtlix.contentlibrary.application.port.in.ContentLibraryUseCase;
import com.project.youtlix.contentlibrary.application.port.in.EpisodeNotFoundException;
import com.project.youtlix.contentlibrary.application.port.in.MovieContentExpectedException;
import com.project.youtlix.contentlibrary.application.port.in.SeasonNotFoundException;
import com.project.youtlix.contentlibrary.application.port.in.SeriesContentExpectedException;
import com.project.youtlix.contentlibrary.domain.model.*;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
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
    public List<ContentResponse> browse(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        currentIdentity(authorization);
        Page requestedPage = toPage(page, size);
        return useCase.browse(requestedPage).stream()
                .map(ContentResponse::from)
                .toList();
    }

    @GetMapping("/library/{id}")
    public Metadata getMetadata(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID id) {
        currentIdentity(authorization);
        return useCase.extendedMetadataOf(new ContentId(id));
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
     * Updates an existing series season.
     */
    @PutMapping("/admin/content/{seriesId}/seasons/{seasonId}")
    public void updateSeason(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID seriesId,
            @PathVariable UUID seasonId,
            @RequestBody SeasonRequest request
    ) {
        requireLibraryAdmin(authorization);
        useCase.updateSeason(new ContentId(seriesId), new SeasonId(seasonId), request.number(), request.title());
    }

    /**
     * Updates an existing series episode.
     */
    @PutMapping("/admin/content/{seriesId}/seasons/{seasonId}/episodes/{episodeId}")
    public void updateEpisode(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID seriesId,
            @PathVariable UUID seasonId,
            @PathVariable UUID episodeId,
            @RequestBody EpisodeRequest request
    ) {
        requireLibraryAdmin(authorization);
        requireCompleteEpisodeFields(request);
        useCase.updateEpisode(
                new ContentId(seriesId),
                new SeasonId(seasonId),
                new EpisodeId(episodeId),
                request.number(),
                request.title(),
                Duration.ofSeconds(request.durationSeconds()),
                new VideoFile(request.videoUri(), request.languages())
        );
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
        if (request.type() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type is required");
        }
        if (request.type() == ContentType.MOVIE) {
            requireCompleteMovieFields(request);
            useCase.updateMovie(
                    new ContentId(id),
                    toMetadata(request),
                    Duration.ofSeconds(request.durationSeconds()),
                    new VideoFile(request.videoUri(), request.languages())
            );
            return;
        }
        rejectMovieFieldsForSeries(request);
        useCase.updateSeriesMetadata(new ContentId(id), toMetadata(request));
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
                request.type(),
                request.genre(),
                request.releaseYear(),
                request.keywords()
        );
    }

    private Metadata toMetadata(
            String title,
            String description,
            String thumbnailUrl,
            ContentType contentType,
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
                    contentType,
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

    @ExceptionHandler(EpisodeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEpisodeNotFound(EpisodeNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(SeriesContentExpectedException.class)
    public ResponseEntity<Map<String, String>> handleSeriesExpected(SeriesContentExpectedException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(MovieContentExpectedException.class)
    public ResponseEntity<Map<String, String>> handleMovieExpected(MovieContentExpectedException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException exception) {
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
}
