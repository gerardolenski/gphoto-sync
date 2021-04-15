package org.gol.photosync.domain.library;

import org.gol.photosync.domain.model.LocalAlbum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class LocalStorageLibraryServiceTest {

    private static final Path LIBRARY_PATH = Path.of("src/test/resources/library");
    public static final String ALBUM_1 = "src/test/resources/library/2020/2020.01 - album 1";
    public static final String ALBUM_2 = "src/test/resources/library/2020/2020.02 - album 2";
    public static final String ALBUM_3 = "src/test/resources/library/2021/2021.12 - album 3";
    public static final String ALBUM_4 = "src/test/resources/library/album 4";

    @Mock
    LibraryProperties libraryProperties;

    LocalStorageLibraryService sut;

    @BeforeEach
    void init() {
        lenient().doReturn(LIBRARY_PATH).when(libraryProperties).getPath();
        sut = new LocalStorageLibraryService(libraryProperties);
    }

    @Test
    void findAlbums() {
        //when, then
        assertThat(sut.findAlbums())
                .containsExactly(
                        Path.of(ALBUM_1),
                        Path.of(ALBUM_2),
                        Path.of(ALBUM_3),
                        Path.of(ALBUM_4));
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("albumProvider")
    void getAlbum(Path albumPath, String expectedTile, int expectedSize) {
        //when
        var album = sut.getAlbum(albumPath);

        //then
        assertThat(album)
                .extracting(LocalAlbum::getPath)
                .isEqualTo(albumPath);
        assertThat(album)
                .extracting(LocalAlbum::getTitle)
                .isEqualTo(expectedTile);
        assertThat(album)
                .extracting(LocalAlbum::getImages)
                .asList()
                .hasSize(expectedSize);
    }

    private static Stream<Arguments> albumProvider() {
        return Stream.of(
                Arguments.of(ALBUM_1, "2020.01 - album 1", 6),
                Arguments.of(ALBUM_2, "2020.02 - album 2", 3),
                Arguments.of(ALBUM_3, "2021.12 - album 3", 9),
                Arguments.of(ALBUM_4, "album 4", 3));
    }
}