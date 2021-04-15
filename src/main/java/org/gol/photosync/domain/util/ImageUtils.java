package org.gol.photosync.domain.util;

import io.vavr.control.Try;
import lombok.AllArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public class ImageUtils {

    private static final String IMAGE_MIME_TYPE = "image";

    public static boolean isImage(Path path) {
        return getImageMimeType(path)
                .isPresent();
    }

    public static Optional<String> getImageMimeType(Path path) {
        return Try.of(() -> Files.probeContentType(path))
                .filter(Objects::nonNull)
                .filter(mime -> mime.contains(IMAGE_MIME_TYPE))
                .map(Optional::of)
                .getOrElse(Optional::empty);
    }
}
