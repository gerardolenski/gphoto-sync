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

    private final LocalAlbumFilterProperties properties = new LocalAlbumFilterProperties(2010, 2020);
    private final FromYearAlbumFilter sut = new FromYearAlbumFilter(properties);

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("positiveTestCaseSupplier")
    @DisplayName("the given album should pass filter [positive]")
    void shouldPassFilter(String testCase, LocalAlbumFilterProperties properties, LocalAlbum album) {
        //given
        var sut = new ToYearAlbumFilter(properties);

        //when, then
        assertThat(sut.shouldBeProcessed(album))
                .isTrue();
    }

    private static Stream<Arguments> positiveTestCaseSupplier() {
        return Stream.of(
                Arguments.of(
                        "year is equal to toYear",
                        new LocalAlbumFilterProperties(2010, 2020),
                        TestLocalAlbum.builder()
                                .title("2020.01 - test")
                                .year(Year.of(2020))
                                .build()),
                Arguments.of("year is before toYear",
                        new LocalAlbumFilterProperties(2010, 2020),
                        TestLocalAlbum.builder()
                                .title("2019.01 - test")
                                .year(Year.of(2019))
                                .build()),
                Arguments.of("album contains only year",
                        new LocalAlbumFilterProperties(2010, 2020),
                        TestLocalAlbum.builder()
                                .year(Year.of(2012))
                                .build()),
                Arguments.of("filter is disabled",
                        new LocalAlbumFilterProperties(2010, -1),
                        TestLocalAlbum.builder()
                                .title("test")
                                .year(Year.of(2000))
                                .build()));
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("negativeTestCaseSupplier")
    @DisplayName("the given album should not pass filter [negative]")
    void shouldNotPassFilter(String testCase, LocalAlbumFilterProperties properties, TestLocalAlbum album) {
        //given
        var sut = new ToYearAlbumFilter(properties);

        //when, then
        assertThat(sut.shouldBeProcessed(album))
                .isFalse();
    }

    private static Stream<Arguments> negativeTestCaseSupplier() {
        return Stream.of(
                Arguments.of(
                        "year is after toYear",
                        new LocalAlbumFilterProperties(2010, 2020),
                        TestLocalAlbum.builder().year(Year.of(2021)).build()),
                Arguments.of("album don't start with year",
                        new LocalAlbumFilterProperties(2010, 2020),
                        TestLocalAlbum.builder().build()));
    }
}