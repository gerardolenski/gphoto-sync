package org.gol.gphotosync.domain.local.filter;

import org.gol.gphotosync.domain.model.LocalAlbum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FromYearAlbumFilterTest {

    private final LocalAlbumFilterProperties properties = new LocalAlbumFilterProperties(2010, 2020);

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("positiveTestCaseSupplier")
    @DisplayName("the given album should pass filter [positive]")
    void shouldPassFilter(String testCase, LocalAlbumFilterProperties properties, LocalAlbum album) {
        //given
        var sut = new FromYearAlbumFilter(properties);

        //when, then
        assertThat(sut.shouldBeProcessed(album))
                .isTrue();
    }

    private static Stream<Arguments> positiveTestCaseSupplier() {
        return Stream.of(
                Arguments.of(
                        "year is equal to fromYear",
                        new LocalAlbumFilterProperties(2010, 2020),
                        LocalAlbum.builder().title("2010.01 - test").build()),
                Arguments.of("year is after fromYear",
                        new LocalAlbumFilterProperties(2010, 2020),
                        LocalAlbum.builder().title("2011.01 - test").build()),
                Arguments.of("album contains only year",
                        new LocalAlbumFilterProperties(2010, 2020),
                        LocalAlbum.builder().title("2012").build()),
                Arguments.of("filter is disabled",
                        new LocalAlbumFilterProperties(-1, 2020),
                        LocalAlbum.builder().title("test").build()));
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("negativeTestCaseSupplier")
    @DisplayName("the given album should not pass filter [negative]")
    void shouldNotPassFilter(String testCase, LocalAlbumFilterProperties properties, LocalAlbum album) {
        //given
        var sut = new FromYearAlbumFilter(properties);

        //when, then
        assertThat(sut.shouldBeProcessed(album))
                .isFalse();
    }

    private static Stream<Arguments> negativeTestCaseSupplier() {
        return Stream.of(
                Arguments.of(
                        "year is before fromYear",
                        new LocalAlbumFilterProperties(2010, 2020),
                        LocalAlbum.builder().title("2009.01 - test").build()),
                Arguments.of("album don't start with year",
                        new LocalAlbumFilterProperties(2010, 2020),
                        LocalAlbum.builder().title("test").build()));
    }

}