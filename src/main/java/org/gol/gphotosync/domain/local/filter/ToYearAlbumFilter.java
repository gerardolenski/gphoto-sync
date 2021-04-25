package org.gol.gphotosync.domain.local.filter;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import org.gol.gphotosync.domain.local.LocalAlbumFilter;
import org.gol.gphotosync.domain.model.LocalAlbum;
import org.springframework.stereotype.Service;

import static org.gol.gphotosync.domain.local.filter.FilterUtils.*;

/**
 * Filters out any album which year is after configured one in 'PHOTO_ALBUMS_TO_YEAR_FILTER' environment variable.
 */
@Slf4j
@Service
class ToYearAlbumFilter implements LocalAlbumFilter {

    private final int toYear;

    ToYearAlbumFilter(LocalAlbumFilterProperties properties) {
        this.toYear = properties.getToYear();
        Option.of(this.toYear)
                .filter(IS_YEAR_DEFINED)
                .onEmpty(() -> log.info("Deactivated TO YEAR local album filter"))
                .peek(year -> log.info("Activated TO YEAR local album filter: toYear={}", year));
    }

    @Override
    public boolean shouldBeProcessed(LocalAlbum album) {
        return Option.of(toYear)
                .filter(IS_YEAR_DEFINED)
                .map(year -> doFilter(album))
                .getOrElse(true);
    }

    private boolean doFilter(LocalAlbum album) {
        return Option.of(retrieveAlbumYear(album))
                .filter(IS_YEAR_DEFINED)
                .map(albumYear -> albumYear <= toYear)
                .filter(IS_TRUE)
                .onEmpty(() -> log.debug("The album was filtered out: albumTitle={}", album.getTitle()))
                .getOrElse(false);
    }
}
