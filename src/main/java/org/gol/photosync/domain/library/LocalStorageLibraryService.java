package org.gol.photosync.domain.library;

import io.vavr.Tuple;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.photosync.domain.model.LocalAlbum;
import org.gol.photosync.domain.model.LocalImage;
import org.gol.photosync.domain.util.ImageUtils;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static org.gol.photosync.domain.util.ImageUtils.getImageMimeType;

@Slf4j
@Service
@RequiredArgsConstructor
class LocalStorageLibraryService implements LocalLibrary {

    private final LibraryProperties libraryProperties;

    @Override
    public List<Path> findAlbums() {
        return Try.of(() -> Files.walk(libraryProperties.getPath())
                .filter(Files::isDirectory)
                .filter(this::containsImages)
                .sorted()
                .collect(toList()))
                .getOrElse(List.of());
    }

    @Override
    public LocalAlbum getAlbum(Path path) {
        return Optional.of(getAllAlbumImages(path))
                .filter(not(List::isEmpty))
                .map(albumImages -> LocalAlbum.builder()
                        .path(path)
                        .title(path.getFileName().toString())
                        .images(albumImages)
                        .build())
                .orElseThrow(() -> new IllegalArgumentException(format("The given path does not contain images: path=%s", path)));
    }

    private boolean containsImages(Path directory) {
        return Try.of(() -> Files.list(directory)
                .anyMatch(ImageUtils::isImage))
                .getOrElse(false);
    }

    private List<LocalImage> getAllAlbumImages(Path albumDirectory) {
        log.debug("Collecting images: albumDirectory={}", albumDirectory);
        return Try.of(() -> Files.list(albumDirectory)
                .map(path -> Tuple.of(path, getImageMimeType(path)))
                .filter(t -> t._2.isPresent())
                .map(t -> LocalImage.builder()
                        .file(t._1.toFile())
                        .fileName(t._1.getFileName().toString())
                        .mimeType(t._2.get())
                        .description(format("%s: %s", albumDirectory.getFileName(), t._1.getFileName()))
                        .build())
                .collect(toList()))
                .getOrElse(List.of());
    }

}
