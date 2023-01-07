package org.gol.gphotosync.infrastructure.library.filesystem;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.local.LocalAlbum;
import org.gol.gphotosync.domain.local.LocalImage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static io.vavr.control.Try.withResources;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;

/**
 * Aggregate working on local file system.
 */
@Slf4j
@Builder
@ToString
@EqualsAndHashCode
class FileSystemAlbum implements LocalAlbum {

    @Getter
    private final String title;
    private final Year year;
    private final Path albumDirectory;

    @Override
    public Optional<Year> getYear() {
        return ofNullable(year);
    }

    @Override
    public List<LocalImage> getImages() {
        //TODO think of caching
        return getAllAlbumImages();
    }

    private List<LocalImage> getAllAlbumImages() {
        log.trace("Collecting images: albumDirectory={}", albumDirectory);
        var images = withResources(() -> Files.list(albumDirectory))
                .of(pathStream -> pathStream
                        .map(FileSystemImageFactory::toLocalImage)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .sorted(comparing(LocalImage::getFileName))
                        .toList())
                .getOrElse(List.of());
        log.trace("Collected images: albumDirectory={}, imagesCount={}", albumDirectory, images.size());
        return images;
    }
}
