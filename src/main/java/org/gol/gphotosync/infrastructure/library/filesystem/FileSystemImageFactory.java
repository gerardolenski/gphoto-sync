package org.gol.gphotosync.infrastructure.library.filesystem;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.gol.gphotosync.domain.local.model.LocalImage;

import java.nio.file.Path;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;
import static org.gol.gphotosync.domain.util.ImageUtils.getImageMimeType;

@NoArgsConstructor(access = PRIVATE)
class FileSystemImageFactory {

    public static final String NO_PATH_ELEMENT_NAME = "";

    static Optional<LocalImage> toLocalImage(@NonNull Path imagePath) {
        return getImageMimeType(imagePath)
                .map(mime -> FileSystemImage.builder()
                        .file(imagePath.toFile())
                        .fileName(imagePath.getFileName().toString())
                        .mimeType(mime)
                        .description(getImageDescription(imagePath))
                        .build());
    }

    private static String getImageDescription(Path imagePath) {
        return format("%s: %s",
                ofNullable(imagePath.getParent())
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .orElse(NO_PATH_ELEMENT_NAME),
                ofNullable(imagePath.getFileName())
                        .map(Path::toString)
                        .orElse(NO_PATH_ELEMENT_NAME));
    }
}
