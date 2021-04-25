package org.gol.gphotosync.domain.local.filter;

import io.vavr.control.Try;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.model.LocalAlbum;

import java.util.function.Predicate;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.substring;

/**
 * Filtering utilities and helpers.
 */
@Slf4j
@NoArgsConstructor(access = PRIVATE)
class FilterUtils {

    public static final Predicate<Integer> IS_YEAR_DEFINED = year -> year >= 0;
    public static final Predicate<Boolean> IS_TRUE = b -> b;

    public static int retrieveAlbumYear(LocalAlbum album) {
        return Try.of(() -> Integer.valueOf(substring(album.getTitle(), 0, 4)))
                .onFailure(e -> log.warn("The album title does not starts with year. Sync is skipped: album={}", album))
                .getOrElse(-1);
    }
}
