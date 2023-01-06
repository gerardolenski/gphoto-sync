package org.gol.gphotosync.infrastructure.library.filesystem;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.local.AlbumFindQuery;
import org.gol.gphotosync.domain.local.LocalAlbum;
import org.gol.gphotosync.domain.local.LocalAlbumFilter;
import org.gol.gphotosync.domain.local.LocalLibraryPort;
import org.gol.gphotosync.domain.util.ImageUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static io.vavr.control.Try.withResources;

/**
 * Implementation working on local file system.
 */
@Slf4j
@RequiredArgsConstructor
class FileSystemLibraryAdapter implements LocalLibraryPort {

    private final @NonNull Path libraryRootPath;

    @Override
    public List<LocalAlbum> findAlbums(AlbumFindQuery query) {
        return withResources(() -> Files.walk(libraryRootPath))
                .of(pathStream -> pathStream
                        .filter(Files::isDirectory)
                        .filter(this::containsImages)
                        .sorted()
                        .map(FileSystemAlbumFactory::toLocalAlbum)
                        .filter(a -> fulfillAllFilters(a, query.filters()))
                        .toList())
                .getOrElse(List.of());
    }

    private boolean containsImages(Path directory) {
        return withResources(() -> Files.list(directory))
                .of(pathStream -> pathStream
                        .anyMatch(ImageUtils::isImage))
                .getOrElse(false);
    }

    private boolean fulfillAllFilters(LocalAlbum album, Set<LocalAlbumFilter> filters) {
        return filters.stream()
                .allMatch(f -> f.shouldBeProcessed(album));
    }
}
