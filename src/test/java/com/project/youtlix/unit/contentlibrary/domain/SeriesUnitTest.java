package com.project.youtlix.unit.contentlibrary.domain;

import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.ContentType;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Season;
import com.project.youtlix.contentlibrary.domain.model.SeasonId;
import com.project.youtlix.contentlibrary.domain.model.Series;
import com.project.youtlix.testsupport.annotation.UnitTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UnitTest
class SeriesUnitTest {

    @Test
    void addSeasonAndRejectDuplicateNumber() {
        Series series = new Series(
                ContentId.newId(),
                new Metadata("Arcane", "Fantasy", ContentType.SERIES, "thumb", Genre.DRAMA, 2024, List.of())
        );
        Season season = new Season(SeasonId.newId(), 1, "Season 1");

        series.addSeason(season);

        assertThat(series.seasonById(season.id())).isPresent();
        assertThatThrownBy(() -> series.addSeason(new Season(SeasonId.newId(), 1, "Duplicate")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
