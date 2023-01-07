package org.gol.gphotosync.domain.local.filter;

import org.gol.gphotosync.domain.local.LocalAlbum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Year;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ToYearAlbumFilterTest {

    private static final Year YEAR_2020 = Year.of(2020);

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("positiveTestCaseSupplier")
    @DisplayName("the given album should pass filter [positive]")
    void shouldPassFilter(String testCase, Year toYear, LocalAlbum album) {
        //given
        var sut = new ToYearAlbumFilter(toYear);

        //when, then
        assertThat(sut.shouldBeProcessed(album))
                .isTrue();
    }

    private static Stream<Arguments> positiveTestCaseSupplier() {
        return Stream.of(
                Arguments.of(
                        "year is equal to toYear",
                        YEAR_2020,
                        TestLocalAlbum.builder()
                                .title("2020.01 - test")
                                .year(Year.of(2020))
                                .build()),
                Arguments.of("year is before toYear",
                        YEAR_2020,
                        TestLocalAlbum.builder()
                                .title("2019.01 - test")
                                .year(Year.of(2019))
                                .build()),
                Arguments.of("album contains only year",
                        YEAR_2020,
                        TestLocalAlbum.builder()
                                .year(Year.of(2012))
                                .build()));
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("negativeTestCaseSupplier")
    @DisplayName("the given album should not pass filter [negative]")
    void shouldNotPassFilter(String testCase, Year toYear, TestLocalAlbum album) {
        //given
        var sut = new ToYearAlbumFilter(toYear);

        //when, then
        assertThat(sut.shouldBeProcessed(album))
                .isFalse();
    }

    private static Stream<Arguments> negativeTestCaseSupplier() {
        return Stream.of(
                Arguments.of(
                        "year is after toYear",
                        YEAR_2020,
                        TestLocalAlbum.builder().year(Year.of(2021)).build()),
                Arguments.of("album don't start with year",
                        YEAR_2020,
                        TestLocalAlbum.builder().build()));
    }
}