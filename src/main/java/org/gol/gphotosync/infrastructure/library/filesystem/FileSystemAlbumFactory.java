package org.gol.gphotosync.infrastructure.library.filesystem;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.local.LocalAlbum;

import java.nio.file.Path;
import java.time.Year;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.substring;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
class FileSystemAlbumFactory {

    public static final String NO_TITLE = "";

    static LocalAlbum toLocalAlbum(@NonNull Path albumDirectory) {
        var title = Optional.ofNullable(albumDirectory.getFileName())
                .map(Path::toString)
                .orElse(NO_TITLE);
        var year = retrieveAlbumYear(title).getOrNull();
        var album = FileSystemAlbum.builder()
                .albumDirectory(albumDirectory)
                .title(title)
                .year(year)
                .build();
        log.trace("Converted path to album: path={}, album={}", albumDirectory, album);
        return album;
    }

    private static Option<Year> retrieveAlbumYear(String title) {
        //TODO parse without exception handling
        return Try.of(() -> Year.parse(substring(title, 0, 4)))
                .onFailure(e -> log.warn("The album title does not starts with year: title={}", title))
                .toOption();
    }
}
