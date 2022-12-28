package org.gol.gphotosync.domain.local.library;

import org.gol.gphotosync.domain.local.LocalAlbumFilter;
import org.gol.gphotosync.domain.model.LocalAlbum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.contains;
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
        sut = new LocalStorageLibraryService(libraryProperties, List.of());
    }

    @Test
    @DisplayName("should correctly find all local library albums [positive]")
    void shouldFindAllAlbums() {
        //when, then
        assertThat(sut.findAlbums())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(ALBUM_1, ALBUM_2, ALBUM_3, ALBUM_4);
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("negativeFilterProvider")
    @DisplayName("should filter out all albums [negative]")
    void shouldFilterOutAllAlbums(String testCase, List<LocalAlbumFilter> filters) {
        //given
        sut = new LocalStorageLibraryService(libraryProperties, filters);

        //when, then
        assertThat(sut.findAlbums())
                .isEmpty();
    }

    private static Stream<Arguments> negativeFilterProvider() {
        return Stream.of(
                Arguments.of("only one negative filter", List.of((LocalAlbumFilter) a -> false)),
                Arguments.of("two negative filter", List.of((LocalAlbumFilter) a -> false, a -> false)),
                Arguments.of("positive and negative filter", List.of((LocalAlbumFilter) a -> true, a -> false)));
    }

    @Test
    @DisplayName("should not filter albums [positive]")
    void shouldNotFilterOutAlbums() {
        //given
        sut = new LocalStorageLibraryService(libraryProperties, List.of(a -> true));

        //when, then
        assertThat(sut.findAlbums())
                .hasSize(4);
    }

    @Test
    @DisplayName("should filter all albums except ALBUM_3 [positive]")
    void shouldFilterParticularAlbum() {
        //given
        sut = new LocalStorageLibraryService(libraryProperties, List.of(a -> contains(a.getTitle(), "album 3")));

        //when, then
        assertThat(sut.findAlbums())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(ALBUM_3);
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("albumProvider")
    @DisplayName("should retrieve all images from album [positive]")
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