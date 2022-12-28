package org.gol.gphotosync.domain.util;

import io.vavr.control.Try;
import lombok.AllArgsConstructor;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import static java.nio.file.Files.probeContentType;
import static lombok.AccessLevel.PRIVATE;

/**
 * Utilities for images processing.
 */
@AllArgsConstructor(access = PRIVATE)
public class ImageUtils {

    private static final String IMAGE_MIME_TYPE = "image";

    /**
     * Tests if the path contains image.
     *
     * @param path the path which is tested
     * @return true if path contains image, otherwise false
     */
    public static boolean isImage(Path path) {
        return getImageMimeType(path)
                .isPresent();
    }

    /**
     * Retrieves the image MIME type from path.
     *
     * @param path the path which is tested
     * @return the optional with image MIME type, empty if path is not the image
     */
    public static Optional<String> getImageMimeType(Path path) {
        return Try.of(() -> probeContentType(path))
                .filter(Objects::nonNull)
                .filter(mime -> mime.contains(IMAGE_MIME_TYPE))
                .map(Optional::of)
                .getOrElse(Optional::empty);
    }
}
