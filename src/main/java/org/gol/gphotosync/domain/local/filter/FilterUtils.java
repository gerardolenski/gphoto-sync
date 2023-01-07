package org.gol.gphotosync.domain.local.filter;

import io.vavr.control.Option;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.local.LocalAlbum;

import java.time.Year;
import java.util.Optional;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import static java.lang.Boolean.TRUE;
import static lombok.AccessLevel.PRIVATE;

/**
 * Filtering utilities and helpers.
 */
@Slf4j
@NoArgsConstructor(access = PRIVATE)
class FilterUtils {

    static final Predicate<Integer> IS_YEAR_DEFINED = year -> year >= 0;

    /**
     * Higher order function which filters album by yesr predicate
     *
     * @param album         the album to filter
     * @param yearPredicate the predicate applied to album year (converted to int)
     * @return true if year fulfills predicate, otherwise false
     */
    static boolean testYear(LocalAlbum album, IntPredicate yearPredicate) {
        return Option.of(album.getYear())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Year::getValue)
                .filter(yearPredicate::test)
                .map(a -> TRUE)
                .onEmpty(() -> log.debug("The album was filtered out: albumTitle={}", album.getTitle()))
                .getOrElse(false);
    }
}
