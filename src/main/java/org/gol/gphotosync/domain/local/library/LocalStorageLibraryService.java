package org.gol.gphotosync.domain.local.library;

import io.vavr.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.local.LocalAlbumFilter;
import org.gol.gphotosync.domain.local.LocalLibraryPort;
import org.gol.gphotosync.domain.model.LocalAlbum;
import org.gol.gphotosync.domain.model.LocalImage;
import org.gol.gphotosync.domain.util.ImageUtils;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static io.vavr.control.Try.withResources;
import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static org.gol.gphotosync.domain.util.ImageUtils.getImageMimeType;

@Slf4j
@Service
@RequiredArgsConstructor
class LocalStorageLibraryService implements LocalLibraryPort {

    private final LibraryProperties libraryProperties;
    private final List<LocalAlbumFilter> filters;

    @Override
    public List<LocalAlbum> findAlbums() {
        return withResources(() -> Files.walk(libraryProperties.getPath()))
                .of(pathStream -> pathStream
                        .filter(Files::isDirectory)
                        .filter(this::containsImages)
                        .sorted()
                        .map(path -> LocalAlbum.builder()
                                .path(path)
                                .title(path.getFileName().toString())
                                .build())
                        .filter(this::fulfillAllFilters)
                        .toList())
                .getOrElse(List.of());
    }

    @Override
    public List<LocalImage> getAlbumImages(LocalAlbum album) {
        return getAllAlbumImages(album.path());
    }

    private boolean containsImages(Path directory) {
        return withResources(() -> Files.list(directory))
                .of(pathStream -> pathStream
                        .anyMatch(ImageUtils::isImage))
                .getOrElse(false);
    }

    private List<LocalImage> getAllAlbumImages(Path albumDirectory) {
        log.trace("Collecting images: albumDirectory={}", albumDirectory);
        var images = withResources(() -> Files.list(albumDirectory))
                .of(pathStream -> pathStream
                        .map(path -> Tuple.of(path, getImageMimeType(path)))
                        .filter(t -> t._2.isPresent())
                        .map(t -> LocalImage.builder()
                                .file(t._1.toFile())
                                .fileName(t._1.getFileName().toString())
                                .mimeType(t._2.get())
                                .description(format("%s: %s", albumDirectory.getFileName(), t._1.getFileName()))
                                .build())
                        .sorted(comparing(LocalImage::fileName))
                        .toList())
                .getOrElse(List.of());
        log.trace("Collected images: albumDirectory={}, imagesCount={}", albumDirectory, images.size());
        return images;
    }

    private boolean fulfillAllFilters(LocalAlbum album) {
        return filters.stream()
                .allMatch(f -> f.shouldBeProcessed(album));
    }
}
