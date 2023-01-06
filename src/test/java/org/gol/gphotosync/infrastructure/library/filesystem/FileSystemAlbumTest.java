package org.gol.gphotosync.infrastructure.library.filesystem;

import org.gol.gphotosync.domain.local.LocalImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.gol.gphotosync.infrastructure.library.filesystem.FileSystemAlbumFactory.toLocalAlbum;

class FileSystemAlbumTest {

    private static final Path ALBUM1_PATH = Path.of("src/test/resources/library/2020/2020.01 - album 1");
    private static final Path ALBUM4_PATH = Path.of("src/test/resources/library/album 4");
    private static final Path ALBUM_WITHOUT_IMAGES_PATH = Path.of("src/test/resources/library/not-album");

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("albumProvider")
    @DisplayName("should correctly load album from path [positive]")
    void shouldCorrectlyLoadAlbum(String testCase, Path albumPath, String expectedTitle, Optional<Year> expectedYear, List<String> expectedImages) {
        //when
        var album = toLocalAlbum(albumPath);

        //then
        assertThat(album.getTitle()).isEqualTo(expectedTitle);
        assertThat(album.getYear()).isEqualTo(expectedYear);
        assertThat(album.getImages())
                .extracting(LocalImage::getFileName)
                .containsExactlyElementsOf(expectedImages);
    }

    public static Stream<Arguments> albumProvider() {
        return Stream.of(
                Arguments.of(
                        "album with year in title and only images in file list",
                        ALBUM1_PATH, "2020.01 - album 1", Optional.of(Year.of(2020)),
                        List.of("test.bmp", "test.gif", "test.jpeg", "test.jpg", "test.png", "test.tiff")),
                Arguments.of(
                        "album without year in title and subset of images in file list",
                        ALBUM4_PATH, "album 4", empty(),
                        List.of("img1.jpg", "img2.jpg", "img3.jpg")),
                Arguments.of(
                        "album without images",
                        ALBUM_WITHOUT_IMAGES_PATH, "not-album", empty(),
                        List.of()),
                Arguments.of(
                        "album from not existent path",
                        Path.of("not/existent/path"), "path", empty(),
                        List.of())
        );
    }

    @Test
    @DisplayName("should throw exception when album path is null [negative]")
    void shouldNotLoadAlbumWhenPathIsNull() {
        //when, then
        assertThatThrownBy(() -> toLocalAlbum(null))
                .isInstanceOf(NullPointerException.class);
    }
}