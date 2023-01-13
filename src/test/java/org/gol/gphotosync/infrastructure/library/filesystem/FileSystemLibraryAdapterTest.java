package org.gol.gphotosync.infrastructure.library.filesystem;

import org.gol.gphotosync.domain.local.model.AlbumFindQuery;
import org.gol.gphotosync.domain.local.model.LocalAlbum;
import org.gol.gphotosync.domain.local.LocalAlbumFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.contains;
import static org.assertj.core.api.Assertions.assertThat;
import static org.gol.gphotosync.infrastructure.library.filesystem.FileSystemAlbumFactory.toLocalAlbum;

@ExtendWith(MockitoExtension.class)
class FileSystemLibraryAdapterTest {

    private static final Path LIBRARY_PATH = Path.of("src/test/resources/library");
    public static final LocalAlbum ALBUM_1 = getAlbum("src/test/resources/library/2020/2020.01 - album 1");
    public static final LocalAlbum ALBUM_2 = getAlbum("src/test/resources/library/2020/2020.02 - album 2");
    public static final LocalAlbum ALBUM_3 = getAlbum("src/test/resources/library/2021/2021.12 - album 3");
    public static final LocalAlbum ALBUM_4 = getAlbum("src/test/resources/library/album 4");
    public static final LocalAlbumFilter NON_MATCH_FILTER = a -> false;
    public static final LocalAlbumFilter ALL_MATCH_FILTER = a -> true;
    public static final LocalAlbumFilter ONLY_2020_FILTER = a -> a.getTitle().startsWith("2020");
    public static final LocalAlbumFilter ONLY_2021_FILTER = a -> a.getTitle().startsWith("2021");

    FileSystemLibraryAdapter sut = new FileSystemLibraryAdapter(LIBRARY_PATH);

    @Test
    @DisplayName("should correctly find all local library albums [positive]")
    void shouldFindAllAlbums() {
        //given
        var query = new AlbumFindQuery(List.of());

        //when, then
        assertThat(sut.findAlbums(query))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(ALBUM_1, ALBUM_2, ALBUM_3, ALBUM_4);
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("negativeFilterProvider")
    @DisplayName("should filter out all albums [negative]")
    void shouldFilterOutAllAlbums(String testCase, List<LocalAlbumFilter> filters) {
        //given
        var query = new AlbumFindQuery(filters);

        //when, then
        assertThat(sut.findAlbums(query))
                .isEmpty();
    }

    private static Stream<Arguments> negativeFilterProvider() {
        return Stream.of(
                Arguments.of("only one negative filter", List.of(NON_MATCH_FILTER)),
                Arguments.of("two negative filters", List.of(ONLY_2020_FILTER, ONLY_2021_FILTER)),
                Arguments.of("positive and negative filter", List.of(ALL_MATCH_FILTER, NON_MATCH_FILTER)));
    }

    @Test
    @DisplayName("should not filter albums [positive]")
    void shouldNotFilterOutAlbums() {
        //given
        var query = new AlbumFindQuery(List.of(ALL_MATCH_FILTER));

        //when, then
        assertThat(sut.findAlbums(query))
                .hasSize(4);
    }

    @Test
    @DisplayName("should filter all albums except ALBUM_3 [positive]")
    void shouldFilterParticularAlbum() {
        //given
        LocalAlbumFilter onlyAlbum3Filter = a -> contains(a.getTitle(), "album 3");
        var query = new AlbumFindQuery(List.of(onlyAlbum3Filter));

        //when, then
        assertThat(sut.findAlbums(query))
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(ALBUM_3);
    }

    private static LocalAlbum getAlbum(String directory) {
        return toLocalAlbum(Path.of(directory));
    }
}