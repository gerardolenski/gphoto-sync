package org.gol.gphotosync.domain.local.filter;

import lombok.AllArgsConstructor;
import org.gol.gphotosync.domain.local.LocalAlbumFilter;

import javax.annotation.Nullable;
import java.time.Year;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

/**
 * Provides static factories for album filtering.
 */
@AllArgsConstructor(access = PRIVATE)
public class LocalAlbumFilterFactory {
    public static List<LocalAlbumFilter> getByYearFilters(@Nullable Year fromYear,
                                                          @Nullable Year toYear) {
        return Stream.of(
                        fromYearAlbumFilter(fromYear),
                        toYearAlbumFilter(toYear))
                .filter(Objects::nonNull)
                .toList();
    }

    private static LocalAlbumFilter fromYearAlbumFilter(Year fromYear) {
        return ofNullable(fromYear)
                .map(FromYearAlbumFilter::new)
                .orElse(null);
    }

    private static LocalAlbumFilter toYearAlbumFilter(Year toYear) {
        return ofNullable(toYear)
                .map(ToYearAlbumFilter::new)
                .orElse(null);
    }
}
