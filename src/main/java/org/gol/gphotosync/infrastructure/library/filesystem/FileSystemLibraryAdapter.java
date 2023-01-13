package org.gol.gphotosync.infrastructure.library.filesystem;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.local.model.AlbumFindQuery;
import org.gol.gphotosync.domain.local.model.LocalAlbum;
import org.gol.gphotosync.domain.local.LocalAlbumFilter;
import org.gol.gphotosync.domain.local.LocalLibraryQueryPort;
import org.gol.gphotosync.domain.util.ImageUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static io.vavr.control.Try.withResources;
import static org.apache.commons.collections4.CollectionUtils.size;

/**
 * Implementation working on local file system.
 */
@Slf4j
@RequiredArgsConstructor
class FileSystemLibraryAdapter implements LocalLibraryQueryPort {

    private final @NonNull Path libraryRootPath;

    @Override
    public List<LocalAlbum> findAlbums(AlbumFindQuery query) {
        log.info("Searching local album library to run synchronization: activeFilters={}", size(query.filters()));
        var albums = withResources(() -> Files.walk(libraryRootPath))
                .of(pathStream -> pathStream
                        .filter(Files::isDirectory)
                        .filter(this::containsImages)
                        .sorted()
                        .map(FileSystemAlbumFactory::toLocalAlbum)
                        .filter(a -> fulfillAllFilters(a, query.filters()))
                        .toList())
                .getOrElse(List.of());
        log.info("Local album library was traversed: albumsCount={}", albums.size());
        return albums;
    }

    private boolean containsImages(Path directory) {
        return withResources(() -> Files.list(directory))
                .of(pathStream -> pathStream
                        .anyMatch(ImageUtils::isImage))
                .getOrElse(false);
    }

    private boolean fulfillAllFilters(LocalAlbum album, List<LocalAlbumFilter> filters) {
        return filters.stream()
                .allMatch(f -> f.shouldBeProcessed(album));
    }
}
