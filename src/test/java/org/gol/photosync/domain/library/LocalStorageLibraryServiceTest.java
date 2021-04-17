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
    public static final LocalAlbum ALBUM_1 = getAlbum("src/test/resources/library/2020/2020.01 - album 1");
    public static final LocalAlbum ALBUM_2 = getAlbum("src/test/resources/library/2020/2020.02 - album 2");
    public static final LocalAlbum ALBUM_3 = getAlbum("src/test/resources/library/2021/2021.12 - album 3");
    public static final LocalAlbum ALBUM_4 = getAlbum("src/test/resources/library/album 4");

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
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(ALBUM_1, ALBUM_2, ALBUM_3, ALBUM_4);
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("albumProvider")
    void getAlbumImages(LocalAlbum album, int expectedSize) {
        //when
        var images = sut.getAlbumImages(album);

        //then
        assertThat(images)
                .hasSize(expectedSize);
    }

    private static Stream<Arguments> albumProvider() {
        return Stream.of(
                Arguments.of(ALBUM_1, 6),
                Arguments.of(ALBUM_2, 3),
                Arguments.of(ALBUM_3, 9),
                Arguments.of(ALBUM_4, 3));
    }

    private static LocalAlbum getAlbum(String directory) {
        var path = Path.of(directory);
        return LocalAlbum.builder()
                .path(path)
                .title(path.getFileName().toString())
                .build();
    }
}