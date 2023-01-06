package org.gol.gphotosync.domain.local.filter;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.local.LocalAlbum;
import org.gol.gphotosync.domain.local.LocalAlbumFilter;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static org.gol.gphotosync.domain.local.filter.FilterUtils.IS_YEAR_DEFINED;

/**
 * Filters out any album which year is before configured one in 'PHOTO_ALBUMS_FROM_YEAR_FILTER' environment variable.
 */
@Slf4j
@Service
class FromYearAlbumFilter implements LocalAlbumFilter {

    private final int fromYear;

    FromYearAlbumFilter(LocalAlbumFilterProperties properties) {
        this.fromYear = properties.getFromYear();
        Option.of(this.fromYear)
                .filter(IS_YEAR_DEFINED)
                .onEmpty(() -> log.info("Deactivated FROM YEAR local album filter"))
                .peek(year -> log.info("Activated FROM YEAR local album filter: fromYear={}", year));
    }

    @Override
    public boolean shouldBeProcessed(LocalAlbum album) {
        return Option.of(fromYear)
                .filter(IS_YEAR_DEFINED)
                .map(year -> doFilter(album))
                .getOrElse(true);
    }

    private boolean doFilter(LocalAlbum album) {
        return Option.of(album.getYear())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Year::getValue)
                .filter(albumYear -> albumYear >= fromYear)
                .map(a -> TRUE)
                .onEmpty(() -> log.debug("The album was filtered out: albumTitle={}", album.getTitle()))
                .getOrElse(false);
    }
}
