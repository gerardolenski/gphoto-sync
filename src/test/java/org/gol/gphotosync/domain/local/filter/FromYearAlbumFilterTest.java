package org.gol.gphotosync.domain.local.filter;

import org.gol.gphotosync.domain.local.LocalAlbum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Year;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FromYearAlbumFilterTest {

    private static final Year YEAR_2010 = Year.of(2010);

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("positiveTestCaseSupplier")
    @DisplayName("the given album should pass filter [positive]")
    void shouldPassFilter(String testCase, Year fromYear, LocalAlbum album) {
        //given
        var sut = new FromYearAlbumFilter(fromYear);

        //when, then
        assertThat(sut.shouldBeProcessed(album))
                .isTrue();
    }

    private static Stream<Arguments> positiveTestCaseSupplier() {
        return Stream.of(
                Arguments.of(
                        "year is equal to fromYear",
                        YEAR_2010,
                        TestLocalAlbum.builder()
                                .title("2010.01 - test")
                                .year(Year.of(2010))
                                .build()),
                Arguments.of("year is after fromYear",
                        YEAR_2010,
                        TestLocalAlbum.builder()
                                .title("2011.01 - test")
                                .year(Year.of(2011))
                                .build()),
                Arguments.of("album contains only year",
                        YEAR_2010,
                        TestLocalAlbum.builder()
                                .year(Year.of(2012))
                                .build()));
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("negativeTestCaseSupplier")
    @DisplayName("the given album should not pass filter [negative]")
    void shouldNotPassFilter(String testCase, Year fromYear, TestLocalAlbum album) {
        //given
        var sut = new FromYearAlbumFilter(fromYear);

        //when, then
        assertThat(sut.shouldBeProcessed(album))
                .isFalse();
    }

    private static Stream<Arguments> negativeTestCaseSupplier() {
        return Stream.of(
                Arguments.of(
                        "year is before fromYear",
                        YEAR_2010,
                        TestLocalAlbum.builder()
                                .title("2009.01 - test")
                                .year(Year.of(2009))
                                .build()),
                Arguments.of("album don't start with year",
                        YEAR_2010,
                        TestLocalAlbum.builder()
                                .title("test")
                                .build()));
    }
}