package com.project.youtlix.unit.contentlibrary.domain;

import com.project.youtlix.contentlibrary.domain.model.Content;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Duration;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Movie;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;
import com.project.youtlix.contentlibrary.domain.model.VideoFile;
import com.project.youtlix.contentlibrary.domain.service.ContentSearchService;
import com.project.youtlix.testsupport.annotation.UnitTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UnitTest
class ContentSearchServiceUnitTest {

    private final ContentSearchService searchService = new ContentSearchService();

    @Test
    void searchByKeywordMatchesTitleAndKeywords() {
        List<Content> catalog = List.of(
                movie("Clean Architecture", "architecture"),
                movie("Comedy Night", "standup")
        );

        assertThat(searchService.searchByKeyword(catalog, "architecture"))
                .extracting(content -> content.metadata().title())
                .containsExactly("Clean Architecture");
    }

    @Test
    void filterCombinesGenreYearAndPhrase() {
        List<Content> catalog = List.of(
                movie("Clean Architecture", Genre.DOCUMENTARY, 2026, "architecture"),
                movie("Architecture Clean", Genre.DOCUMENTARY, 2026, "design"),
                movie("Old Documentary", Genre.DOCUMENTARY, 2020, "archive")
        );

        assertThat(searchService.filter(catalog, new SearchCriteria("architecture", Genre.DOCUMENTARY, 2025, 2026)))
                .extracting(content -> content.metadata().title())
                .containsExactlyInAnyOrder("Clean Architecture", "Architecture Clean");
    }

    @Test
    void filterRejectsInvalidYearRange() {
        assertThatThrownBy(() -> new SearchCriteria(null, Genre.DOCUMENTARY, 2027, 2020))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void browseReturnsOnlyAvailableContent() {
        Movie available = movie("Available", "hero");
        available.publish();
        Movie withdrawn = movie("Withdrawn", "hero");
        withdrawn.publish();
        withdrawn.withdraw();

        assertThat(searchService.browse(List.of(available, withdrawn)))
                .extracting(content -> content.metadata().title())
                .containsExactly("Available");
    }

    private Movie movie(String title, String keyword) {
        return movie(title, Genre.DOCUMENTARY, 2026, keyword);
    }

    private Movie movie(String title, Genre genre, int year, String keyword) {
        Movie movie = new Movie(
                ContentId.newId(),
                new Metadata(title, "desc", "thumb", genre, year, List.of(new Keyword(keyword))),
                Duration.ofSeconds(120),
                new VideoFile("cdn://" + title, List.of("pl"))
        );
        movie.publish();
        return movie;
    }
}
