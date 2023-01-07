package org.gol.gphotosync.domain.local.filter;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.local.LocalAlbum;
import org.gol.gphotosync.domain.local.LocalAlbumFilter;

import java.time.Year;
import java.util.function.IntPredicate;

import static org.gol.gphotosync.domain.local.filter.FilterUtils.testYear;

/**
 * Filters out any album which year is after desired year.
 */
@Slf4j
class ToYearAlbumFilter implements LocalAlbumFilter {

    private final Year toYear;
    private final IntPredicate isEqualOrBefore;

    ToYearAlbumFilter(Year toYear) {
        this.toYear = toYear;
        this.isEqualOrBefore = albumYear -> albumYear <= toYear.getValue();
        log.info("Activated TO YEAR local album filter: toYear={}", toYear);
    }

    @Override
    public boolean shouldBeProcessed(LocalAlbum album) {
        return Option.of(toYear)
                .map(year -> testYear(album, isEqualOrBefore))
                .getOrElse(true);
    }
}
