package org.gol.gphotosync.domain.local.filter;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.local.model.LocalAlbum;
import org.gol.gphotosync.domain.local.LocalAlbumFilter;

import java.time.Year;
import java.util.function.IntPredicate;

import static org.gol.gphotosync.domain.local.filter.FilterUtils.testYear;

/**
 * Filters out any album which year is before desired year.
 */
@Slf4j
class FromYearAlbumFilter implements LocalAlbumFilter {

    private final Year fromYear;
    private final IntPredicate isEqualOrAfter;

    FromYearAlbumFilter(Year fromYear) {
        this.fromYear = fromYear;
        this.isEqualOrAfter = albumYear -> albumYear >= fromYear.getValue();
        log.info("Activated FROM YEAR local album filter: fromYear={}", fromYear);
    }

    @Override
    public boolean shouldBeProcessed(LocalAlbum album) {
        return Option.of(fromYear)
                .map(year -> testYear(album, isEqualOrAfter))
                .getOrElse(true);
    }
}
